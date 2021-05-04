package mil.af.abms.midas.api.assertion;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.AssertionStatus;
import mil.af.abms.midas.enums.AssertionType;

public class AssertionTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final Set<Tag> tags = Set.of(Builder.build(Tag.class).with(t -> t.setId(2L)).get());
    private final Set<Comment> comments = Set.of(Builder.build(Comment.class).with(c -> c.setId(2L)).get());
    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setText("First"))
            .with(a -> a.setType(AssertionType.OBJECTIVE))
            .with(a -> a.setCreationDate(CREATION_DATE))
            .with(a -> a.setComments(comments))
            .with(a -> a.setCreatedBy(createdBy)).get();
    private final AssertionDTO assertionDTO = Builder.build(AssertionDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setText("First"))
            .with(d -> d.setType(AssertionType.OBJECTIVE))
            .with(d -> d.setStatus(AssertionStatus.NOT_STARTED))
            .with(d -> d.setCreationDate(CREATION_DATE))
            .with(d -> d.setCommentIds(Set.of(2L)))
            .with(d -> d.setChildIds(Set.of()))
            .with(d -> d.setCreatedById(createdBy.getId())).get();

    @Test
    public void should_have_all_assertion_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Assertion.class, fields::add);

        assertThat(fields.size()).isEqualTo(AssertionDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_set_and_get_properties() {
        assertThat(assertion.getId()).isEqualTo(1L);
        assertThat(assertion.getType()).isEqualTo(AssertionType.OBJECTIVE);
        assertThat(assertion.getCreatedBy()).isEqualTo(createdBy);
        assertThat(assertion.getText()).isEqualTo("First");
        assertThat(assertion.getComments()).isEqualTo(comments);
        assertThat(assertion.getCreationDate()).isEqualTo(CREATION_DATE);
    }

    @Test
    public void should_return_dto() {
        assertThat(assertion.toDto()).isEqualTo(assertionDTO);
    }

    @Test
    public void should_be_equal() {
        Assertion assertion2 = new Assertion();
        BeanUtils.copyProperties(assertion, assertion2);

        assertEquals(assertion, assertion);
        assertNotEquals(assertion, null);
        assertNotEquals(assertion, new User());
        assertNotEquals(assertion, new Assertion());
        assertEquals(assertion, assertion2);
    }

    @Test
    public void should_return_with_null_created_by() {
        Assertion assertionNullCreatedBy = new Assertion();
        BeanUtils.copyProperties(assertion, assertionNullCreatedBy);
        assertionNullCreatedBy.setCreatedBy(null);

        assertThat(assertionNullCreatedBy.toDto().getCreatedById()).isEqualTo(null);
    }

}
