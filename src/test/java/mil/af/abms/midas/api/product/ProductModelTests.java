package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.team.TeamEntity;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.UserEntity;

public class ProductModelTests {

    @MockBean
    TeamService teamService;

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();

    TeamEntity team = Builder.build(TeamEntity.class)
            .with(t -> t.setId(3L)).get();

    ProductEntity expectedProduct = Builder.build(ProductEntity.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setDescription("testDescription"))
            .with(p -> p.setTeam(team))
            .with(p -> p.setIsArchived(true))
            .with(p -> p.setGitlabProjectId(2L))
            .with(p -> p.setCreationDate(CREATION_DATE)).get();

    ProductDTO expectedProductDTO = Builder.build(ProductDTO.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setDescription("testDescription"))
            .with(p -> p.setIsArchived(true))
            .with(p -> p.setTeamId(3L))
            .with(p -> p.setGitlabProjectId(2L))
            .with(p -> p.setCreationDate(CREATION_DATE)).get();

    @Test
    public void should_Set_And_Get_Properties() {
        assertThat(expectedProduct.getId()).isEqualTo(1L);
        assertThat(expectedProduct.getName()).isEqualTo("MIDAS");
        assertThat(expectedProduct.getTeam()).isEqualTo(team);
        assertThat(expectedProduct.getDescription()).isEqualTo("testDescription");
        assertTrue(expectedProduct.getIsArchived());
        assertThat(expectedProduct.getGitlabProjectId()).isEqualTo(2L);
        assertThat(expectedProduct.getCreationDate()).isEqualTo(CREATION_DATE);
    }

    @Test
    public void can_Return_DTO() {
        assertThat(expectedProduct.toDto()).isEqualTo(expectedProductDTO);
    }

    @Test
    public void should_Be_Equal() {
        ProductEntity product2 = Builder.build(ProductEntity.class)
                .with(p -> p.setName("MIDAS")).get();

        assertTrue(expectedProduct.equals(expectedProduct));
        assertFalse(expectedProduct.equals(new UserEntity()));
        assertTrue(expectedProduct.equals(product2));
    }
}
