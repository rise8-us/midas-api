package mil.af.abms.midas.api.product;

import javax.transaction.Transactional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductIsArchivedDTO;
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
                .with(p -> p.setName(createProductDTO.getName()))
                .with(p -> p.setDescription(createProductDTO.getDescription()))
                .with(p -> p.setVisionStatement(createProductDTO.getVisionStatement()))
                .with(p -> p.setProblemStatement(createProductDTO.getProblemStatement()))
                .with(p -> p.setProductManager(userService.findByIdOrNull(createProductDTO.getProductManagerId())))
                .with(p -> p.setTags(createProductDTO.getTagIds().stream().map(tagService::getObject)
                        .collect(Collectors.toSet())))
                .with(p -> p.setPortfolio(portfolioService.findByIdOrNull(createProductDTO.getPortfolioId())))
                .with(p -> p.setProjects(createProductDTO.getProjectIds().stream()
                        .map(projectService::getObject).collect(Collectors.toSet())))
                .get();

        newProduct = repository.save(newProduct);
        projectService.addProductToProjects(newProduct, newProduct.getProjects());

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
        product.setVisionStatement(updateProductDTO.getVisionStatement());
        product.setProblemStatement(updateProductDTO.getProblemStatement());
        product.setPortfolio(portfolioService.findByIdOrNull(updateProductDTO.getPortfolioId()));
        product.setTags(updateProductDTO.getTagIds().stream()
                .map(tagService::getObject).collect(Collectors.toSet()));
        product.setProjects(updateProductDTO.getProjectIds().stream()
                .map(projectService::getObject).collect(Collectors.toSet()));

        projectService.addProductToProjects(product, product.getProjects());

        return repository.save(product);
    }
    
    @Transactional
    public Product updateIsArchivedById(Long id, UpdateProductIsArchivedDTO updateProductIsArchivedDTO) {
        Product product = getObject(id);
        product.setIsArchived(updateProductIsArchivedDTO.getIsArchived());
        
        return repository.save(product);
    }

}
