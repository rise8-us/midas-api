package mil.af.abms.midas.api.product;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class ProductService extends AbstractCRUDService<Product, ProductDTO, ProductRepository> {

    private TeamService teamService;
    private TagService tagService;

    @Autowired
    public ProductService(ProductRepository repository, TeamService teamService, TagService tagService) {
        super(repository, Product.class, ProductDTO.class);
        this.teamService = teamService;
        this.tagService = tagService;
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

        if (!updateProductDTO.getTagIds().isEmpty()) {
            Set<Tag> tags = updateProductDTO.getTagIds().stream().map(tagService::getObject).collect(Collectors.toSet());
            foundProduct.setTags(tags);
        } else {
            foundProduct.setTags(null);
        }

        foundProduct.setName(updateProductDTO.getName());
        foundProduct.setDescription(updateProductDTO.getDescription());
        foundProduct.setGitlabProjectId(updateProductDTO.getGitlabProjectId());
        foundProduct.setIsArchived(updateProductDTO.getIsArchived());

        return repository.save(foundProduct);
    }

    public void removeTagFromProducts(Long tagId, Set<Product> products) {
        products.forEach(p -> removeTagFromProduct(tagId, p));
    }

    @Transactional
    public void removeTagFromProduct(Long tagId, Product product) {
        Set<Tag> tagsToKeep = product.getTags().stream().filter(t -> !t.getId().equals(tagId)).collect(Collectors.toSet());
        product.setTags(tagsToKeep);
        repository.save(product);
    }

}
