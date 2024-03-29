package mil.af.abms.midas.api.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.ProgressionStatus;

class AssertionTests {

    private final Set<Comment> comments = Set.of(Builder.build(Comment.class).with(c -> c.setId(2L)).get());
    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(3L)).get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setText("First"))
            .with(a -> a.setProduct(product))
            .with(a -> a.setComments(comments))
            .with(a -> a.setDueDate(TimeConversion.getLocalDateOrNullFromObject("2021-07-09")))
            .with(a -> a.setComments(comments))
            .with(a -> a.setCreatedBy(createdBy)).get();
    private final AssertionDTO assertionDTO = Builder.build(AssertionDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setText("First"))
            .with(d -> d.setStatus(ProgressionStatus.NOT_STARTED))
            .with(d -> d.setProductId(product.getId()))
            .with(d -> d.setCreationDate(assertion.getCreationDate()))
            .with(d -> d.setCommentIds(Set.of(2L)))
            .with(d -> d.setMeasureIds(List.of()))
            .with(d -> d.setChildren(List.of()))
            .with(d -> d.setPassedToIds(List.of()))
            .with(d -> d.setIsArchived(false))
            .with(d -> d.setDueDate(TimeConversion.getLocalDateOrNullFromObject("2021-07-09")))
            .with(d -> d.setCreatedById(createdBy.getId())).get();

    @Test
    void should_have_all_assertion_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Assertion.class, fields::add);

        assertThat(fields).hasSize(AssertionDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_set_and_get_properties() {
        assertThat(assertion.getId()).isEqualTo(1L);
        assertThat(assertion.getCreatedBy()).isEqualTo(createdBy);
        assertThat(assertion.getText()).isEqualTo("First");
        assertThat(assertion.getComments()).isEqualTo(comments);
    }

    @Test
    void should_return_dto() {
        assertThat(assertion.toDto()).isEqualTo(assertionDTO);
    }

    @Test
    void should_be_equal() {
        Assertion assertion2 = new Assertion();
        BeanUtils.copyProperties(assertion, assertion2);

        assertEquals(assertion, assertion);
        assertNotEquals(assertion, null);
        assertNotEquals(assertion, new User());
        assertNotEquals(assertion, new Assertion());
        assertEquals(assertion, assertion2);
    }

    @Test
    void should_return_with_null_created_by() {
        Assertion assertionNullCreatedBy = new Assertion();
        BeanUtils.copyProperties(assertion, assertionNullCreatedBy);
        assertionNullCreatedBy.setCreatedBy(null);

        assertNull(assertionNullCreatedBy.toDto().getCreatedById());
    }

}
