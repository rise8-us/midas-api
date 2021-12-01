package mil.af.abms.midas.api.comment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.comment.dto.CommentDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;

class CommentTests {

    private static final int ENTITY_DTO_FIELD_OFFSET = 2;

    private final User createdBy = Builder.build(User.class).with(u -> u.setId(1L)).get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setText("First"))
            .with(a -> a.setCreatedBy(createdBy)).get();
    private final Comment parentComment = Builder.build(Comment.class).with(c -> c.setId(55L)).get();
    private final Comment comment = Builder.build(Comment.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setText("New Idea"))
            .with(c -> c.setParent(parentComment))
            .with(c -> c.setAssertion(assertion))
            .with(c -> c.setCreatedBy(createdBy))
            .get();
    private final CommentDTO commentDTO = Builder.build(CommentDTO.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setText("New Idea"))
            .with(c -> c.setParentId(parentComment.getId()))
            .with(c -> c.setAuthor(createdBy.toDto()))
            .with(d -> d.setCreationDate(comment.getCreationDate()))
            .with(d -> d.setChildren(Set.of()))
            .get();

    @Test
    void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Comment.class, fields::add);

        assertThat(fields.size()).isEqualTo(CommentDTO.class.getDeclaredFields().length + ENTITY_DTO_FIELD_OFFSET);
    }

    @Test
    void should_be_equal() {
        Comment comment2 = new Comment();
        BeanUtils.copyProperties(comment, comment2);

        assertEquals(comment, comment);
        assertNotEquals(comment, null);
        assertNotEquals(comment, new User());
        assertNotEquals(comment, new Comment());
        assertEquals(comment, comment2);
    }

    @Test
    void should_get_properties() {
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getText()).isEqualTo("New Idea");
        assertThat(comment.getParent()).isEqualTo(parentComment);
        assertThat(comment.getCreatedBy()).isEqualTo(createdBy);
        assertThat(comment.getAssertion()).isEqualTo(assertion);
    }

    @Test
    void can_return_dto() { assertThat(comment.toDto()).isEqualTo(commentDTO); }

}
