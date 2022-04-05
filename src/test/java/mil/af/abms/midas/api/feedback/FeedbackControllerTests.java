package mil.af.abms.midas.api.feedback;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.feedback.dto.CreateFeedbackDTO;
import mil.af.abms.midas.api.feedback.dto.UpdateFeedbackDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.FeedbackRating;

@WebMvcTest({FeedbackController.class})
public class FeedbackControllerTests extends ControllerTestHarness {

    @MockBean
    private FeedbackService feedbackService;

    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Feedback feedback = Builder.build(Feedback.class)
            .with(f -> f.setCreatedBy(createdBy))
            .with(f -> f.setRating(FeedbackRating.GOOD))
            .with(f -> f.setRelatedTo("OGSM"))
            .with(f -> f.setNotes("Notes of an ordinary nature"))
            .get();
    CreateFeedbackDTO createFeedbackDTO = new CreateFeedbackDTO(
            feedback.getRating(),
            feedback.getRelatedTo(),
            feedback.getNotes()
    );
    UpdateFeedbackDTO updateFeedbackDTO = new UpdateFeedbackDTO(
            FeedbackRating.EXCELLENT,
            "Notes of an extraordinary nature"
    );

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_feedback() throws Exception {
        when(feedbackService.create(any(CreateFeedbackDTO.class))).thenReturn(feedback);

        mockMvc.perform(post("/api/feedback")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createFeedbackDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.notes").value("Notes of an ordinary nature"));
    }

    @Test
    void should_update_by_id() throws Exception {
        var newFeedback = new Feedback();
        BeanUtils.copyProperties(updateFeedbackDTO, newFeedback);
        when(feedbackService.updateById(any(), any(UpdateFeedbackDTO.class))).thenReturn(newFeedback);

        mockMvc.perform(put("/api/feedback/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateFeedbackDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.notes").value("Notes of an extraordinary nature"));
    }

    @Test
    void should_throw_errors_due_to_null_parameters_on_create() throws Exception {
        var feedback2 = new CreateFeedbackDTO();
        BeanUtils.copyProperties(createFeedbackDTO, feedback2);
        feedback2.setRating(null);
        feedback2.setRelatedTo(null);

        mockMvc.perform(post("/api/feedback")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(feedback2))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").exists())
                .andExpect(jsonPath("$.errors[2]").doesNotExist());
    }

    @Test
    void should_throw_errors_due_to_null_parameters_on_update() throws Exception {
        var feedback3 = new UpdateFeedbackDTO();
        BeanUtils.copyProperties(updateFeedbackDTO, feedback3);
        feedback3.setRating(null);

        mockMvc.perform(put("/api/feedback/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(feedback3))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").doesNotExist());
    }

    @Test
    void should_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/feedback/1"))
                .andExpect(status().isOk());

        verify(feedbackService, times(1)).deleteById(1L);
    }

}
