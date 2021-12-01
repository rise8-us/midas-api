package mil.af.abms.midas.api.feedback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.feedback.dto.CreateFeedbackDTO;
import mil.af.abms.midas.api.feedback.dto.UpdateFeedbackDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.FeedbackRating;

@ExtendWith(SpringExtension.class)
@Import(FeedbackService.class)
class FeedbackServiceTests {

    @SpyBean
    FeedbackService feedbackService;
    @MockBean
    UserService userService;
    @MockBean
    FeedbackRepository repository;
    @Captor
    ArgumentCaptor<Feedback> captor;

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

    @Test
    void should_create_feedback() {
        when(userService.getUserBySecContext()).thenReturn(createdBy);

        when(feedbackService.findByIdOrNull(feedback.getId())).thenReturn(feedback);
        when(repository.save(any())).thenReturn(new Feedback());

        feedbackService.create(createFeedbackDTO);

        verify(repository, times(1)).save(captor.capture());
        var feedbackSaved = captor.getValue();

        assertThat(feedbackSaved.getNotes()).isEqualTo(createFeedbackDTO.getNotes());
        assertThat(feedbackSaved.getRating()).isEqualTo(createFeedbackDTO.getRating());
    }

    @Test
    void should_update_comment_by_id() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(feedback));
        when(userService.getUserBySecContext()).thenReturn(createdBy);
        feedbackService.updateById(1L, updateFeedbackDTO);

        verify(repository, times(1)).save(captor.capture());
        var feedbackSaved = captor.getValue();

        assertThat(feedbackSaved.getNotes()).isEqualTo(updateFeedbackDTO.getNotes());
        assertThat(feedbackSaved.getRating()).isEqualTo(updateFeedbackDTO.getRating());
        assertThat(feedbackSaved.getEditedBy()).isNotNull();
        assertThat(feedbackSaved.getEditedAt()).isNotNull();
    }


}
