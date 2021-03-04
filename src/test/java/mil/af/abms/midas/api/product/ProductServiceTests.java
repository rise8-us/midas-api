package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
import mil.af.abms.midas.api.product.dto.UpdateProductTeamDTO;
import mil.af.abms.midas.api.team.TeamEntity;
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
    ProductRepository productRepository;

    @Captor
    ArgumentCaptor<ProductEntity> productCaptor;

    ProductEntity product = Builder.build(ProductEntity.class)
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setId(1L)).get();
    TeamEntity team = Builder.build(TeamEntity.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setName("Team")).get();

    @Test
    public void should_Create_Product() {
        CreateProductDTO createProductDTO = new CreateProductDTO("MIDAS", "Product Description", 2L);

        when(productRepository.save(product)).thenReturn(new ProductEntity());

        productService.create(createProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        ProductEntity productSaved = productCaptor.getValue();

        assertThat(productSaved.getName()).isEqualTo(createProductDTO.getName());
        assertThat(productSaved.getDescription()).isEqualTo(createProductDTO.getDescription());
        assertThat(productSaved.getGitlabProjectId()).isEqualTo(createProductDTO.getGitlabProjectId());
    }

    @Test
    public void should_Find_By_Name() throws EntityNotFoundException {
        when(productRepository.findByName("MIDAS")).thenReturn(Optional.of(product));

        assertThat(productService.findByName("MIDAS")).isEqualTo(product);
    }

    @Test
    public void should_Throw_Error_Find_By_Name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                productService.findByName("MIDAS"));
    }

    @Test
    public void should_Update_Product_By_Id() {
        UpdateProductDTO updateProductDTO = new UpdateProductDTO(
                "MIDAS_TWO", "New Description", true, 22L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateById(1L, updateProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        ProductEntity productSaved = productCaptor.getValue();

        assertThat(productSaved.getName()).isEqualTo(updateProductDTO.getName());
        assertThat(productSaved.getDescription()).isEqualTo(updateProductDTO.getDescription());
        assertThat(productSaved.getIsArchived()).isEqualTo(updateProductDTO.getIsArchived());
        assertThat(productSaved.getGitlabProjectId()).isEqualTo(updateProductDTO.getGitlabProjectId());
    }

    @Test
    public void should_Update_Product_Team_By_Team_Id() {
        UpdateProductTeamDTO updateProductTeamDTO = new UpdateProductTeamDTO(2L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(teamService.getObject(any())).thenReturn(team);
        when(productRepository.save(product)).thenReturn(product);

        productService.updateProductTeamByTeamId(1L, updateProductTeamDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        ProductEntity productSaved = productCaptor.getValue();

        assertThat(productSaved.getTeam().getId()).isEqualTo(updateProductTeamDTO.getTeamId());
    }
}
