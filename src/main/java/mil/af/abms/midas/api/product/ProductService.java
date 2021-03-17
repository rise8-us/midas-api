package mil.af.abms.midas.api.product;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class ProductService extends AbstractCRUDService<Product, ProductDTO, ProductRepository> {

    private TeamService teamService;

    @Autowired
    public ProductService(ProductRepository repository, TeamService teamService) {
        super(repository, Product.class, ProductDTO.class);
        this.teamService = teamService;
    }

    @Transactional
    public Product create(CreateProductDTO createProductDTO) {
        Product newProduct = Builder.build(Product.class)
                .with(p -> p.setName(createProductDTO.getName()))
                .with(p -> p.setDescription(createProductDTO.getDescription()))
                .with(p -> p.setGitlabProjectId(createProductDTO.getGitlabProjectId())).get();

        return repository.save(newProduct);
    }

    @Transactional
    public Product findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Product.class.getSimpleName(), "name", name));
    }

    @Transactional
    public Product updateById(Long id, UpdateProductDTO updateProductDTO) {
        Product foundProduct = getObject(id);

        if (updateProductDTO.getTeamId() != null) {
            Team team = teamService.getObject(updateProductDTO.getTeamId());
            foundProduct.setTeam(team);
        } else {
            foundProduct.setTeam(null);
        }

        foundProduct.setName(updateProductDTO.getName());
        foundProduct.setDescription(updateProductDTO.getDescription());
        foundProduct.setGitlabProjectId(updateProductDTO.getGitlabProjectId());
        foundProduct.setIsArchived(updateProductDTO.getIsArchived());

        return repository.save(foundProduct);
    }

}
