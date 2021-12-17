package mil.af.abms.midas.api.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
import mil.af.abms.midas.api.comment.dto.UpdateCommentDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.measure.Measure;
import mil.af.abms.midas.api.measure.MeasureService;
import mil.af.abms.midas.api.user.User;

@WebMvcTest({CommentController.class})
public class CommentControllerTests extends ControllerTestHarness {

    @MockBean
    CommentService commentService;
    @MockBean
    AssertionService assertionService;
    @MockBean
    MeasureService measureService;

    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Assertion assertion = Builder.build(Assertion.class).with(a -> a.setId(1L)).get();
    private final Measure measure = Builder.build(Measure.class).with(m -> m.setId(2L)).get();
    private final Comment parentComment = Builder.build(Comment.class).with(c -> c.setId(55L)).get();
    private final CreateCommentDTO createDTOA = new CreateCommentDTO(parentComment.getId(), assertion.getId(), null, "something new");
    private final CreateCommentDTO createDTOM = new CreateCommentDTO(parentComment.getId(), null, measure.getId(), "something new");
    private final CreateCommentDTO createDTOBoth = new CreateCommentDTO(parentComment.getId(), assertion.getId(), measure.getId(), "something new");
    private final CreateCommentDTO createDTONeither = new CreateCommentDTO(parentComment.getId(), null, null, "something new");
    private final UpdateCommentDTO updateDTO = new UpdateCommentDTO("something updated");
    private final Comment comment = Builder.build(Comment.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setText("New Idea"))
            .with(c -> c.setParent(parentComment))
            .with(c -> c.setCreatedBy(createdBy)).get();

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @ParameterizedTest
    @CsvSource(value = { "true", "false" })
    public void should_create_comment(boolean isAssertion) throws Exception {
        when(commentService.create(any(CreateCommentDTO.class), any())).thenReturn(comment);
        when(assertionService.getById(assertion.getId())).thenReturn(Optional.of(assertion));
        when(measureService.getById(measure.getId())).thenReturn(Optional.of(measure));
        when(commentService.existsById(parentComment.getId())).thenReturn(true);

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(isAssertion ? createDTOA : createDTOM))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value(comment.getText()));
    }

    @ParameterizedTest
    @CsvSource(value = { "true , Comment must have only an assertion or measure ", "false , No measure or assertion exists" })
    public void should_not_create_comment(boolean isBoth, String errorMessage) throws Exception {
        when(commentService.create(any(CreateCommentDTO.class), any())).thenReturn(comment);
        when(assertionService.getById(assertion.getId())).thenReturn(Optional.of(assertion));
        when(measureService.getById(measure.getId())).thenReturn(Optional.of(measure));
        when(commentService.existsById(parentComment.getId())).thenReturn(true);

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(isBoth ? createDTOBoth : createDTONeither))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(errorMessage));
    }

    @Test
    public void should_update_comment_by_id() throws Exception {
        when(commentService.updateById(any(), any(UpdateCommentDTO.class))).thenReturn(comment);
        when(commentService.existsById(55L)).thenReturn(true);

        mockMvc.perform(put("/api/comments/1")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(updateDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value(comment.getText()));
    }

    @Test
    public void should_delete_comment_by_id() throws Exception {
        doNothing().when(commentService).deleteById(any());
        when(commentService.existsById(55L)).thenReturn(true);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isOk());
    }

}
