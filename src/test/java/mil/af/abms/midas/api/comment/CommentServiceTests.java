package mil.af.abms.midas.api.comment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
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

import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
import mil.af.abms.midas.api.comment.dto.UpdateCommentDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;

@ExtendWith(SpringExtension.class)
@Import(CommentService.class)
public class CommentServiceTests {

    @Autowired
    CommentService commentService;
    @MockBean
    UserService userService;
    @MockBean
    AssertionService assertionService;
    @MockBean
    CommentRepository commentRepository;
    @Captor
    ArgumentCaptor<Comment> commentCaptor;

    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Comment parentComment = Builder.build(Comment.class).with(c -> c.setId(55L)).get();
    private final Assertion assertion = Builder.build(Assertion.class).with(a -> a.setId(1L)).get();
    private final Comment comment = Builder.build(Comment.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setText("New Idea"))
            .with(c -> c.setParent(parentComment))
            .with(c -> c.setAssertion(assertion))
            .with(c -> c.setCreatedBy(createdBy)).get();

    @Test
    public void should_create_comment() {
        CreateCommentDTO createDTO = new CreateCommentDTO(parentComment.getId(), assertion.getId(), "something new");

        when(commentRepository.findById(createDTO.getParentId())).thenReturn(Optional.of(parentComment));
        when(assertionService.findByIdOrNull(assertion.getId())).thenReturn(assertion);
        when(commentRepository.save(comment)).thenReturn(new Comment());

        commentService.create(createDTO);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment commentSaved = commentCaptor.getValue();

        assertThat(commentSaved.getText()).isEqualTo(createDTO.getText());
        assertThat(commentSaved.getAssertion()).isEqualTo(assertion);
        assertThat(commentSaved.getParent()).isEqualTo(parentComment);
    }

    @Test
    public void should_update_comment_by_id() {
        UpdateCommentDTO updateDTO = new UpdateCommentDTO(parentComment.getId(), assertion.getId(), "something updated");

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        commentService.updateById(1L, updateDTO);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment commentSaved = commentCaptor.getValue();

        assertThat(commentSaved.getText()).isEqualTo(updateDTO.getText());
    }

}
