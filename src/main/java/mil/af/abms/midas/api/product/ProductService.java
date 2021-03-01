package mil.af.abms.midas.api.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductTeamDTO;
import mil.af.abms.midas.api.team.TeamEntity;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class ProductService extends AbstractCRUDService<ProductEntity, ProductDTO, ProductRepository> {

    private TeamService teamService;

    @Autowired
    public ProductService(ProductRepository repository, TeamService teamService) {
        super(repository, ProductEntity.class, ProductDTO.class);
        this.teamService = teamService;
    }

    public ProductEntity create(CreateProductDTO createProductDTO) {
        ProductEntity newProduct = Builder.build(ProductEntity.class)
                .with(p -> p.setName(createProductDTO.getName()))
                .with(p -> p.setDescription(createProductDTO.getDescription()))
                .with(p -> p.setGitlabProjectId(createProductDTO.getGitlabProjectId())).get();

        return repository.save(newProduct);
    }

    public ProductEntity findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(ProductEntity.class.getSimpleName(), "name", name));
    }

    public ProductEntity updateById(Long id, UpdateProductDTO updateProductDTO) {
        ProductEntity foundProduct = getObject(id);
        foundProduct.setName(updateProductDTO.getName());
        foundProduct.setDescription(updateProductDTO.getDescription());
        foundProduct.setGitlabProjectId(updateProductDTO.getGitlabProjectId());
        foundProduct.setIsArchived(updateProductDTO.getIsArchived());

        return repository.save(foundProduct);
    }

    public ProductEntity updateProductTeamByTeamId(Long id, UpdateProductTeamDTO updateProductTeamDTO) {
        ProductEntity foundProduct = getObject(id);
        TeamEntity team = teamService.getObject(updateProductTeamDTO.getTeamId());
        foundProduct.setTeam(team);

        return repository.save(foundProduct);
    }
}
