package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.User;

public class ProductTests {

    @MockBean
    TeamService teamService;

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final Set<Tag> tags = Set.of(Builder.build(Tag.class).with(u -> u.setId(2L)).get());
    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(3L)).get();
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(3L)).get();

    Product expectedProduct = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setDescription("testDescription"))
            .with(p -> p.setTeam(team))
            .with(p -> p.setIsArchived(true))
            .with(p -> p.setGitlabProjectId(2L))
            .with(p -> p.setTags(tags))
            .with(p -> p.setProductJourneyMap(0L))
            .with(p -> p.setPortfolio(portfolio))
            .with(p -> p.setCreationDate(CREATION_DATE)).get();

    ProductDTO expectedProductDTO = Builder.build(ProductDTO.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setDescription("testDescription"))
            .with(p -> p.setIsArchived(true))
            .with(p -> p.setTeamId(3L))
            .with(p -> p.setGitlabProjectId(2L))
            .with(p -> p.setProductJourneyMap(0L))
            .with(p -> p.setTagIds(Set.of(2L)))
            .with(p -> p.setPortfolioId(portfolio.getId()))
            .with(p -> p.setCreationDate(CREATION_DATE)).get();

    @Test
    public void should_have_all_productDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Product.class, fields::add);

        assertThat(fields.size()).isEqualTo(ProductDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_set_and_get_properties() {
        assertThat(expectedProduct.getId()).isEqualTo(1L);
        assertThat(expectedProduct.getName()).isEqualTo("MIDAS");
        assertThat(expectedProduct.getTeam()).isEqualTo(team);
        assertThat(expectedProduct.getDescription()).isEqualTo("testDescription");
        assertThat(expectedProduct.getProductJourneyMap()).isEqualTo(0L);
        assertTrue(expectedProduct.getIsArchived());
        assertThat(expectedProduct.getGitlabProjectId()).isEqualTo(2L);
        assertThat(expectedProduct.getCreationDate()).isEqualTo(CREATION_DATE);
    }

    @Test
    public void should_return_dto() {
        assertThat(expectedProduct.toDto()).isEqualTo(expectedProductDTO);
    }

    @Test
    public void should_return_dto_null_team() {
        Product productNullTeam = new Product();
        BeanUtils.copyProperties(expectedProduct, productNullTeam);
        productNullTeam.setTeam(null);

        assertThat(productNullTeam.toDto().getTeamId()).isEqualTo(null);
    }

    @Test
    public void should_return_dto_null_portfolio() {
        Product productNullPortfolio = new Product();
        BeanUtils.copyProperties(expectedProduct, productNullPortfolio);
        productNullPortfolio.setPortfolio(null);

        assertThat(productNullPortfolio.toDto().getPortfolioId()).isEqualTo(null);
    }

    @Test
    public void should_be_equal() {
        Product product2 = Builder.build(Product.class)
                .with(p -> p.setName("MIDAS")).get();

        assertTrue(expectedProduct.equals(expectedProduct));
        assertFalse(expectedProduct.equals(null));
        assertFalse(expectedProduct.equals(new User()));
        assertFalse(expectedProduct.equals(new Product()));
        assertTrue(expectedProduct.equals(product2));
    }
}
