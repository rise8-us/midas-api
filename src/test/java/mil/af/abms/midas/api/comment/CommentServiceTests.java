package mil.af.abms.midas.api.comment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
import mil.af.abms.midas.api.comment.dto.UpdateCommentDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.measure.Measure;
import mil.af.abms.midas.api.measure.MeasureService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;

@ExtendWith(SpringExtension.class)
@Import(CommentService.class)
class CommentServiceTests {

    @SpyBean
    CommentService commentService;
    @MockBean
    UserService userService;
    @MockBean
    AssertionService assertionService;
    @MockBean
    MeasureService measureService;
    @MockBean
    CommentRepository commentRepository;
    @Captor
    ArgumentCaptor<Comment> commentCaptor;
    @Captor
    ArgumentCaptor<Long> longCaptor;
    @Captor
    ArgumentCaptor<AssertionDTO> assertionCaptor;
    @Captor
    ArgumentCaptor<String> stringCaptor;
    @MockBean
    SimpMessageSendingOperations websocket;

    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Comment parentComment = Builder.build(Comment.class).with(c -> c.setId(55L)).get();
    private final Assertion assertion = Builder.build(Assertion.class).with(a -> a.setId(1L)).get();
    private final Measure measure = Builder.build(Measure.class).with(m -> m.setId(1L)).get();
    private final Comment comment = Builder.build(Comment.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setText("New Idea"))
            .with(c -> c.setParent(parentComment))
            .with(c -> c.setCreatedBy(createdBy))
            .get();

    @ParameterizedTest
    @CsvSource(value = { "true", "false" })
    void should_create_comment(boolean isAssertion) {
        CreateCommentDTO createDTOA = new CreateCommentDTO(parentComment.getId(), assertion.getId(), null, "DTO A");
        CreateCommentDTO createDTOM = new CreateCommentDTO(parentComment.getId(), null, measure.getId(), "DTO B");

        when(userService.getUserBySecContext()).thenReturn(createdBy);
        when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));
        when(assertionService.findByIdOrNull(assertion.getId())).thenReturn(assertion);
        when(measureService.findByIdOrNull(measure.getId())).thenReturn(measure);
        when(commentRepository.save(any())).thenReturn(new Comment());

        commentService.create(isAssertion ? createDTOA : createDTOM);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment commentSaved = commentCaptor.getValue();

        assertThat(commentSaved.getText()).isEqualTo(isAssertion ? createDTOA.getText() : createDTOM.getText());
        assertThat(commentSaved.getAssertion()).isEqualTo(isAssertion ? assertion : null);
        assertThat(commentSaved.getMeasure()).isEqualTo(isAssertion ? null : measure);
        assertThat(commentSaved.getParent()).isEqualTo(parentComment);
    }

    @Test
    void should_update_comment_by_id() {
        UpdateCommentDTO updateDTO = new UpdateCommentDTO("something updated");

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userService.getUserBySecContext()).thenReturn(createdBy);
        commentService.updateById(1L, updateDTO);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment commentSaved = commentCaptor.getValue();

        assertThat(commentSaved.getText()).isEqualTo(updateDTO.getText());
        assertThat(commentSaved.getEditedBy()).isEqualTo(createdBy);
    }

    @Test
    void should_recursively_deleteById() {

        var parentComment = new Comment();
        BeanUtils.copyProperties(comment, parentComment);
        var childComment =  Builder.build(Comment.class)
                .with(c -> c.setId(5L))
                .with(c -> c.setParent(parentComment))
                .with(c -> c.setAssertion(assertion))
                .get();
        parentComment.getChildren().add(childComment);

        doReturn(parentComment).when(commentService).findById(1L);
        doReturn(childComment).when(commentService).findById(5L);
        doNothing().when(commentRepository).deleteById(1L);
        doNothing().when(commentRepository).deleteById(5L);

        commentService.deleteById(1L);

        verify(commentRepository, times(2)).deleteById(longCaptor.capture());

        assertThat(longCaptor.getAllValues().get(0)).isEqualTo(5L);
        assertThat(longCaptor.getAllValues().get(1)).isEqualTo(1L);
    }

    @Test
    void should_deleteComment() {
        var commentToDelete = new Comment();
        commentToDelete.setId(42L);

        doReturn(commentToDelete).when(commentService).findById(42L);

        commentService.deleteComment(commentToDelete);
        verify(commentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void should_update_relation() {
        commentService.updateRelation(assertion, comment);

        verify(websocket, times(1)).convertAndSend(stringCaptor.capture(), assertionCaptor.capture());

        assertThat(assertion.toDto()).isEqualTo(assertionCaptor.getValue());
        assertThat("/topic/update_assertion").isEqualTo(stringCaptor.getValue());
    }

    @Test
    void should_removeRelationIfExists() {
        commentService.removeRelationIfExists(assertion, comment);

        verify(websocket, times(1)).convertAndSend(stringCaptor.capture(), assertionCaptor.capture());

        assertThat(assertion.toDto()).isEqualTo(assertionCaptor.getValue());
        assertThat("/topic/update_assertion").isEqualTo(stringCaptor.getValue());
    }

    @Test
    void should_skip_relation_update() {

        commentService.updateRelation(null, comment);

        verify(websocket, times(0)).convertAndSend(stringCaptor.capture(), assertionCaptor.capture());
    }

}
