package mil.af.abms.midas.api.comment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
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

    @SpyBean
    CommentService commentService;
    @MockBean
    UserService userService;
    @MockBean
    AssertionService assertionService;
    @MockBean
    CommentRepository commentRepository;
    @Captor
    ArgumentCaptor<Comment> commentCaptor;
    @Captor
    ArgumentCaptor<Long> longCaptor;

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
        UpdateCommentDTO updateDTO = new UpdateCommentDTO("something updated");

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        commentService.updateById(1L, updateDTO);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment commentSaved = commentCaptor.getValue();

        assertThat(commentSaved.getText()).isEqualTo(updateDTO.getText());
    }

    @Test
    public void should_recursively_deleteById() {

        Comment parentComment = new Comment();
        BeanUtils.copyProperties(comment, parentComment);
        Comment childCommment =  Builder.build(Comment.class)
                .with(c -> c.setId(5L))
                .with(c -> c.setParent(parentComment))
                .get();
        parentComment.getChildren().add(childCommment);

        doReturn(parentComment).when(commentService).getObject(1L);
        doReturn(childCommment).when(commentService).getObject(5L);
        doNothing().when(commentRepository).deleteById(1L);
        doNothing().when(commentRepository).deleteById(5L);

        commentService.deleteById(1L);

        verify(commentRepository, times(2)).deleteById(longCaptor.capture());

        assertThat(longCaptor.getAllValues().get(0)).isEqualTo(5L);
        assertThat(longCaptor.getAllValues().get(1)).isEqualTo(1L);

    }

}
