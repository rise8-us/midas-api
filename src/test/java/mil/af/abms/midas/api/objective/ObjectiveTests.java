package mil.af.abms.midas.api.objective;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.objective.dto.ObjectiveDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;

public class ObjectiveTests {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime COMPLETE = NOW.plusWeeks(1L);

    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .get();
    private final User user = Builder.build(User.class)
            .with(p -> p.setId(2L))
            .get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(3L))
            .get();
    private final Objective objective = Builder.build(Objective.class)
            .with(o -> o.setId(4L))
            .with(o -> o.setProduct(product))
            .with(o -> o.setCreatedBy(user))
            .with(o -> o.setAssertions(Set.of(assertion)))
            .with(o -> o.setCreationDate(NOW))
            .with(o -> o.setCompletedDate(COMPLETE))
            .get();
    private final ObjectiveDTO objectiveDTO = Builder.build(ObjectiveDTO.class)
            .with(d -> d.setId(4L))
            .with(d -> d.setProductId(product.getId()))
            .with(d -> d.setCreatedById(user.getId()))
            .with(d -> d.setAssertionIds(Set.of(assertion.getId())))
            .with(d -> d.setCreationDate(NOW))
            .with(d -> d.setCompletedDate(COMPLETE))
            .get();

    @Test
    public void should_have_all_objectiveDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Objective.class, fields::add);

        assertThat(fields.size()).isEqualTo(ObjectiveDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_set_and_get_properties() {
        assertThat(objective.getId()).isEqualTo(4L);
        assertThat(objective.getCreatedBy()).isEqualTo(user);
    }

    @Test
    public void should_return_dto() {
        assertThat(objective.toDto()).isEqualTo(objectiveDTO);
    }

    @Test
    public void should_be_equal() {
        Objective objective2 = Builder.build(Objective.class)
                .with(p -> p.setId(4L)).get();

        assertEquals(objective, objective);
        assertNotEquals(objective, null);
        assertNotEquals(objective, new User());
        assertNotEquals(objective, new Objective());
        assertEquals(objective, objective2);
    }

}
