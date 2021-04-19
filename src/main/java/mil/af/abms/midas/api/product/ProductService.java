package mil.af.abms.midas.api.product;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductIsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class ProductService extends AbstractCRUDService<Product, ProductDTO, ProductRepository> {

    private UserService userService;
    private ProjectService projectService;
    private TagService tagService;
    private PortfolioService portfolioService;

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
    public void setPortfolioService(PortfolioService portfolioService) { this.portfolioService = portfolioService; }
    
    @Transactional
    public Product create(CreateProductDTO createProductDTO) {
        Product newProduct = Builder.build(Product.class)
                .with(a -> a.setName(createProductDTO.getName()))
                .with(a -> a.setDescription(createProductDTO.getDescription()))
                .with(a -> a.setProductManager(userService.findByIdOrNull(createProductDTO.getProductManagerId())))
                .with(a -> a.setTags(createProductDTO.getTagIds().stream().map(tagService::getObject)
                        .collect(Collectors.toSet())))
                .with(a -> a.setPortfolio(portfolioService.findByIdOrNull(createProductDTO.getPortfolioId())))
                .get();

        Set<Project> projects = createProductDTO.getProjectIds().stream().map(projectService::getObject).collect(Collectors.toSet());
        newProduct.setProjects(projects);
        newProduct = repository.save(newProduct);

        for (Project project : projects) {
           projectService.addProductToProject(newProduct, project);
        }

        return newProduct;
    }

    @Transactional
    public Product findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Product.class.getSimpleName(), "name", name));
    }

    @Transactional
    public Product updateById(Long id, UpdateProductDTO updateProductDTO) {

        Product product = getObject(id);
        product.setName(updateProductDTO.getName());
        product.setProductManager(userService.findByIdOrNull(updateProductDTO.getProductManagerId()));
        product.setDescription(updateProductDTO.getDescription());
        product.setPortfolio(portfolioService.findByIdOrNull(updateProductDTO.getPortfolioId()));
        product.setTags(updateProductDTO.getTagIds().stream()
                .map(tagService::getObject).collect(Collectors.toSet()));
        product.setProjects(updateProductDTO.getProjectIds().stream()
                .map(projectService::getObject).collect(Collectors.toSet()));

        return repository.save(product);
    }
    
    @Transactional
    public Product updateIsArchivedById(Long id, UpdateProductIsArchivedDTO updateProductIsArchivedDTO) {
        Product product = getObject(id);
        product.setIsArchived(updateProductIsArchivedDTO.getIsArchived());
        
        return repository.save(product);
    }

}
