package mil.af.abms.midas.api.problem;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.problem.dto.ProblemDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;

public class ProblemTests {

    private static final LocalDateTime TEST_TIME = LocalDateTime.now();

    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Product product = Builder.build(Product.class).with(p -> p.setId(3L)).get();
    private final Problem problem = Builder.build(Problem.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setText("Not enough time"))
            .with(p -> p.setProduct(product))
            .with(p -> p.setCreatedBy(createdBy))
            .with(p -> p.setIsCurrent(true))
            .with(p -> p.setCreationDate(TEST_TIME)).get();
    private final ProblemDTO problemDTO = Builder.build(ProblemDTO.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setText("Not enough time"))
            .with(p -> p.setProductId(product.getId()))
            .with(p -> p.setCreatedById(createdBy.getId()))
            .with(p -> p.setIsCurrent(true))
            .with(p -> p.setCreationDate(TEST_TIME)).get();

    @Test
    public void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Problem.class, fields::add);

        assertThat(fields.size()).isEqualTo(ProblemDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_be_equal() {
        Problem problem2 = new Problem();
        BeanUtils.copyProperties(problem, problem2);

        assertEquals(problem, problem);
        assertNotEquals(problem, null);
        assertNotEquals(problem, new User());
        assertNotEquals(problem, new Problem());
        assertEquals(problem, problem2);
    }

    @Test
    public void should_get_properties() {
        assertThat(problem.getId()).isEqualTo(1L);
        assertThat(problem.getText()).isEqualTo("Not enough time");
        assertThat(problem.getCreationDate()).isEqualTo(TEST_TIME);
        assertThat(problem.getProduct()).isEqualTo(product);
        assertThat(problem.getIsCurrent()).isEqualTo(true);
        assertThat(problem.getCreatedBy()).isEqualTo(createdBy);
    }

    @Test
    public void can_return_dto() {
        assertThat(problem.toDto()).isEqualTo(problemDTO);
    }

    @Test
    public void should_return_dto_with_null_fields() {
        Problem nullJoinFields = new Problem();
        BeanUtils.copyProperties(product, nullJoinFields);
        nullJoinFields.setProduct(null);
        nullJoinFields.setCreatedBy(null);

        assertThat(nullJoinFields.toDto().getProductId()).isEqualTo(null);
        assertThat(nullJoinFields.toDto().getCreatedById()).isEqualTo(null);
    }
}
