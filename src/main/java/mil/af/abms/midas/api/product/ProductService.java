package mil.af.abms.midas.api.product;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.gitlabconfig.GitlabConfigService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductIsArchivedDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.project.dto.ArchiveProjectDTO;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class ProductService extends AbstractCRUDService<Product, ProductDTO, ProductRepository> {

    private UserService userService;
    private ProjectService projectService;
    private TagService tagService;
    private GitlabConfigService gitlabConfigService;

    public ProductService(ProductRepository repository) {
        super(repository, Product.class, ProductDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }
    @Autowired
    public void setProjectService(ProjectService projectService) { this.projectService = projectService; }
    @Autowired
    public void setTagService(TagService tagService) { this.tagService = tagService; }
    @Autowired
    public void setGitlabConfigService(GitlabConfigService gitlabConfigService) { this.gitlabConfigService = gitlabConfigService; }

    @Transactional
    public Product create(CreateProductDTO dto) {
        var pmCandidate =  userService.findByIdOrNull(dto.getProductManagerId());
        User productManager = pmCandidate != null ? pmCandidate : userService.getUserBySecContext();
        var newProduct = Builder.build(Product.class)
                .with(p -> p.setName(dto.getName()))
                .with(p -> p.setType(dto.getType()))
                .with(p -> p.setDescription(dto.getDescription()))
                .with(p -> p.setProductManager(productManager))
                .with(p -> p.setGitlabGroupId(dto.getGitlabGroupId()))
                .with(p -> p.setGitlabConfig(gitlabConfigService.findByIdOrNull(dto.getGitlabConfigId())))
                .with(p -> p.setTags(dto.getTagIds().stream().map(tagService::getObject)
                        .collect(Collectors.toSet())))
                .with(p -> p.setParent(findByIdOrNull(dto.getParentId())))
                .with(p -> p.setChildren(dto.getChildIds().stream()
                        .map(this::getObject).collect(Collectors.toSet())))
                .with(p -> p.setProjects(dto.getProjectIds().stream()
                        .map(projectService::getObject).collect(Collectors.toSet())))
                .get();

        newProduct = repository.save(newProduct);
        projectService.addProductToProjects(newProduct, newProduct.getProjects());
        addParentToChildren(newProduct, newProduct.getChildren());

        return newProduct;
    }

    @Transactional
    public Product findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Product.class.getSimpleName(), "name", name));
    }

    @Transactional
    public Product updateById(Long id, UpdateProductDTO dto) {

        var product = getObject(id);
        var originalProjects = product.getProjects();

        product.setName(dto.getName());
        product.setProductManager(userService.findByIdOrNull(dto.getProductManagerId()));
        product.setDescription(dto.getDescription());
        product.setParent(findByIdOrNull(dto.getParentId()));
        product.setGitlabGroupId(dto.getGitlabGroupId());
        product.setGitlabConfig(gitlabConfigService.findByIdOrNull(dto.getGitlabConfigId()));
        product.setTags(dto.getTagIds().stream()
                .map(tagService::getObject).collect(Collectors.toSet()));
        product.setProjects(dto.getProjectIds().stream()
                .map(projectService::getObject).collect(Collectors.toSet()));
        product.setChildren(dto.getChildIds().stream()
                .map(this::getObject).collect(Collectors.toSet()));

        projectService.updateProjectsWithProduct(originalProjects, product.getProjects(), product);
        addParentToChildren(product, product.getChildren());

        return repository.save(product);
    }
    
    @Transactional
    public Product updateIsArchivedById(Long id, UpdateProductIsArchivedDTO updateProductIsArchivedDTO) {
        var product = getObject(id);
        var archiveDTO = Builder.build(ArchiveProjectDTO.class)
                .with(d -> d.setIsArchived(updateProductIsArchivedDTO.getIsArchived())).get();
        Set<Project> projects = product.getProjects().stream().map(
                p -> projectService.archive(p.getId(), archiveDTO)).collect(Collectors.toSet());
        product.setProjects(projects);
        product.setIsArchived(updateProductIsArchivedDTO.getIsArchived());
        return repository.save(product);
    }

    public void addParentToChildren(Product parent, Set<Product> childern) {
        childern.forEach(child -> addParentToChild(parent, child));
    }

    public void addParentToChild(Product parent, Product child) {
        child.setParent(parent);
        repository.save(child);
    }

}
