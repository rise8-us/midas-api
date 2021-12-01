package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.ProductType;
import mil.af.abms.midas.enums.RoadmapType;

class ProductTests {

    private final User lead = Builder.build(User.class).with(u -> u.setId(2L)).get();
    private final Team team = Builder.build(Team.class).with(t -> t.setId(4L)).get();
    private final Set<Project> projects = Set.of(Builder.build(Project.class).with(p -> p.setId(3L)).get());
    private final Product portfolio = Builder.build(Product.class).with(p -> p.setId(3L)).get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setDescription("test product"))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setOwner(lead))
            .with(p -> p.setParent(portfolio))
            .with(p -> p.setChildren(Set.of()))
            .with(p -> p.setProjects(projects))
            .with(p -> p.setType(ProductType.PRODUCT))
            .with(p -> p.setTeams(Set.of(team)))
            .with(p -> p.setRoadmapType(RoadmapType.GITLAB))
            .get();
    private final ProductDTO productDTO = Builder.build(ProductDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setName("Midas"))
            .with(d -> d.setDescription("test product"))
            .with(d -> d.setCreationDate(product.getCreationDate()))
            .with(d -> d.setIsArchived(false))
            .with(d -> d.setOwnerId(lead.getId()))
            .with(d -> d.setParentId(portfolio.getId()))
            .with(d -> d.setTags(new HashSet<>()))
            .with(d -> d.setProjectIds(Set.of(3L)))
            .with(d -> d.setChildren(Set.of()))
            .with(d -> d.setType(ProductType.PRODUCT))
            .with(d -> d.setTeamIds(Set.of(4L)))
            .with(p -> p.setRoadmapType(RoadmapType.GITLAB))
            .get();

    @Test
    void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Product.class, fields::add);

        assertThat(fields.size()).isEqualTo(ProductDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Product product2 = new Product();
        BeanUtils.copyProperties(product, product2);

        assertEquals(product, product);
        assertNotEquals(product, null);
        assertNotEquals(product, new User());
        assertNotEquals(product, new Product());
        assertEquals(product, product2);
    }

    @Test
    void should_get_properties() {
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Midas");
        assertThat(product.getDescription()).isEqualTo("test product");
        assertFalse(product.getIsArchived());
        assertThat(product.getOwner()).isEqualTo(lead);
        assertThat(product.getParent()).isEqualTo(portfolio);
        assertThat(product.getProjects()).isEqualTo(projects);
    }

    @Test
    void can_return_dto() {
        assertThat(product.toDto()).isEqualTo(productDTO);
    }

    @Test
    void should_return_dto_with_null_fields() {
        Product nullAppAndProduct = new Product();
        BeanUtils.copyProperties(product, nullAppAndProduct);
        nullAppAndProduct.setOwner(null);
        nullAppAndProduct.setParent(null);

        assertNull(nullAppAndProduct.toDto().getOwnerId());
        assertNull(nullAppAndProduct.toDto().getParentId());
    }
}
