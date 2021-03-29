package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(ProductService.class)
public class ProductServiceTests {

    @Autowired
    ProductService productService;
    @MockBean
    TeamService teamService;
    @MockBean
    TagService tagService;
    @MockBean
    ProductRepository productRepository;

    @Captor
    ArgumentCaptor<Product> productCaptor;

    Tag tagInProduct = Builder.build(Tag.class)
            .with(t -> t.setId(22L))
            .with(t -> t.setLabel("TagInProduct")).get();
    Tag tagTwoInProduct = Builder.build(Tag.class)
            .with(t -> t.setId(21L))
            .with(t -> t.setLabel("TagTwoInProduct")).get();
    Team team = Builder.build(Team.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setName("Team")).get();
    Product product = Builder.build(Product.class)
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setTeam(team))
            .with(p -> p.setTags(Set.of(tagInProduct, tagTwoInProduct)))
            .with(p -> p.setId(1L)).get();
    Tag tag = Builder.build(Tag.class)
            .with(t -> t.setId(3L))
            .with(t -> t.setLabel("Tag"))
            .with(t -> t.setProducts(Set.of(product))).get();

    @Test
    public void should_create_product() {
        CreateProductDTO createProductDTO = new CreateProductDTO("MIDAS", 2L, "Product Description");

        when(productRepository.save(product)).thenReturn(new Product());

        productService.create(createProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getName()).isEqualTo(createProductDTO.getName());
        assertThat(productSaved.getDescription()).isEqualTo(createProductDTO.getDescription());
        assertThat(productSaved.getGitlabProjectId()).isEqualTo(createProductDTO.getGitlabProjectId());
    }

    @Test
    public void should_find_by_name() throws EntityNotFoundException {
        when(productRepository.findByName("MIDAS")).thenReturn(Optional.of(product));

        assertThat(productService.findByName("MIDAS")).isEqualTo(product);
    }

    @Test
    public void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                productService.findByName("MIDAS"));
    }

    @Test
    public void should_update_product_by_id() {
        UpdateProductDTO updateProductDTO = new UpdateProductDTO(
                "MIDAS_TWO", 5L, 22L, Set.of(tag.getId()), "New Description", true);
        Team newTeam = new Team();
        BeanUtils.copyProperties(team, newTeam);
        newTeam.setId(22L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(teamService.getObject(updateProductDTO.getTeamId())).thenReturn(newTeam);

        productService.updateById(1L, updateProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getName()).isEqualTo(updateProductDTO.getName());
        assertThat(productSaved.getDescription()).isEqualTo(updateProductDTO.getDescription());
        assertThat(productSaved.getIsArchived()).isEqualTo(updateProductDTO.getIsArchived());
        assertThat(productSaved.getGitlabProjectId()).isEqualTo(updateProductDTO.getGitlabProjectId());
        assertThat(productSaved.getTeam().getId()).isEqualTo(updateProductDTO.getTeamId());
    }

    @Test
    public void should_set_team_to_null() {
        UpdateProductDTO updateDTO = Builder.build(UpdateProductDTO.class)
                .with(d -> d.setName("products"))
                .with(d -> d.setGitlabProjectId(1L))
                .with(d -> d.setTagIds(Set.of())).get();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(tagService.getObject(1L)).thenReturn(tag);

        productService.updateById(1L, updateDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getTeam()).isEqualTo(null);
    }

    @Test
    public void should_set_tag_to_null() {
        UpdateProductDTO updateDTO = Builder.build(UpdateProductDTO.class)
                .with(d -> d.setTagIds(Set.of())).get();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(tagService.getObject(1L)).thenReturn(tag);

        productService.updateById(1L, updateDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

       assertThat(productSaved.getTags()).isEqualTo(null);
    }

    @Test
    public void should_remove_tag_from_products() {
        productService.removeTagFromProducts(tagInProduct.getId(), Set.of(product));

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();
        assertThat(productSaved.getTags()).isEqualTo(Set.of(tagTwoInProduct));
    }

    @Test
    public void should_remove_tag_from_product() {
       productService.removeTagFromProduct(tagInProduct.getId(), product);

       Set<Tag> tagsToKeep = Set.of(tagTwoInProduct);

       verify(productRepository, times(1)).save(productCaptor.capture());
       Product productSaved = productCaptor.getValue();
        assertThat(productSaved.getTags()).isEqualTo(tagsToKeep);
    }

}
