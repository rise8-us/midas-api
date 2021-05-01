package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.ogsm.Ogsm;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.ProductType;

public class ProductTests {

    private static final LocalDateTime TEST_TIME = LocalDateTime.now();

    private final User lead = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Set<Project> projects = Set.of(Builder.build(Project.class).with(p -> p.setId(3L)).get());
    private final Set<Ogsm> ogsms = Set.of(Builder.build(Ogsm.class).with(o -> o.setId(5L)).get());
    private final Product portfolio = Builder.build(Product.class).with(p -> p.setId(3L)).get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setDescription("test product"))
            .with(p -> p.setCreationDate(TEST_TIME))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setProductManager(lead))
            .with(p -> p.setParent(portfolio))
            .with(p -> p.setChildren(Set.of()))
            .with(p -> p.setProjects(projects))
            .with(p -> p.setOgsms(ogsms))
            .with(p -> p.setType(ProductType.APPLICATION))
            .get();
    private final ProductDTO productDTO = Builder.build(ProductDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setName("Midas"))
            .with(d -> d.setDescription("test product"))
            .with(d -> d.setCreationDate(TEST_TIME))
            .with(d -> d.setIsArchived(false))
            .with(d -> d.setProductManagerId(lead.getId()))
            .with(d -> d.setParentId(portfolio.getId()))
            .with(d -> d.setTags(new HashSet<>()))
            .with(d -> d.setProjectIds(Set.of(3L)))
            .with(d -> d.setChildren(Set.of()))
            .with(d -> d.setType(ProductType.APPLICATION))
            .with(d -> d.setOgsmIds(ogsms.stream().map(Ogsm::getId).collect(Collectors.toSet())))
            .get();

    @Test
    public void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Product.class, fields::add);

        assertThat(fields.size()).isEqualTo(ProductDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_be_equal() {
        Product product2 = new Product();
        BeanUtils.copyProperties(product, product2);

        assertEquals(product, product);
        assertNotEquals(product, null);
        assertNotEquals(product, new User());
        assertNotEquals(product, new Product());
        assertEquals(product, product2);
    }

    @Test
    public void should_get_properties() {
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Midas");
        assertThat(product.getDescription()).isEqualTo("test product");
        assertThat(product.getCreationDate()).isEqualTo(TEST_TIME);
        assertFalse(product.getIsArchived());
        assertThat(product.getProductManager()).isEqualTo(lead);
        assertThat(product.getParent()).isEqualTo(portfolio);
        assertThat(product.getProjects()).isEqualTo(projects);
    }

    @Test
    public void can_return_dto() {
        assertThat(product.toDto()).isEqualTo(productDTO);
    }

    @Test
    public void should_return_dto_with_null_fields() {
        Product nullAppAndProduct = new Product();
        BeanUtils.copyProperties(product, nullAppAndProduct);
        nullAppAndProduct.setProductManager(null);
        nullAppAndProduct.setParent(null);

        assertThat(nullAppAndProduct.toDto().getProductManagerId()).isEqualTo(null);
        assertThat(nullAppAndProduct.toDto().getParentId()).isEqualTo(null);
    }
}
