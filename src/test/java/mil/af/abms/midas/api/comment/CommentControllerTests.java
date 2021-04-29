package mil.af.abms.midas.api.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
import mil.af.abms.midas.api.comment.dto.UpdateCommentDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;

@WebMvcTest({CommentController.class})
public class CommentControllerTests extends ControllerTestHarness {

    @MockBean
    CommentService commentService;

    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
        private final Assertion assertion = Builder.build(Assertion.class).with(a -> a.setId(1L)).get();
    private final Comment parentComment = Builder.build(Comment.class).with(c -> c.setId(55L)).get();
    private final CreateCommentDTO createDTO = new CreateCommentDTO(parentComment.getId(), assertion.getId(), "something new");
    private final UpdateCommentDTO updateDTO = new UpdateCommentDTO(parentComment.getId(), assertion.getId(), "something updated");
    private final Comment comment = Builder.build(Comment.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setText("New Idea"))
            .with(c -> c.setParent(parentComment))
            .with(c -> c.setAssertion(assertion))
            .with(c -> c.setCreatedBy(createdBy)).get();

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_comment() throws Exception {
        when(commentService.create(any(CreateCommentDTO.class))).thenReturn(comment);

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value(comment.getText()));
    }

    @Test
    public void should_update_comment_by_id() throws Exception {
        when(commentService.updateById(any(), any(UpdateCommentDTO.class))).thenReturn(comment);

        mockMvc.perform(put("/api/comments/1")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(updateDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value(comment.getText()));
    }

}
