package mil.af.abms.midas.api.feedback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.feedback.dto.FeedbackDTO;
import mil.af.abms.midas.api.feedback.dto.UpdateFeedbackDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.FeedbackRating;

class FeedbackTests {

    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Feedback feedback = Builder.build(Feedback.class)
            .with(f -> f.setId(1L))
            .with(f -> f.setCreatedBy(createdBy))
            .with(f -> f.setRating(FeedbackRating.GOOD))
            .with(f -> f.setRelatedTo("OGSM"))
            .with(f -> f.setNotes("Notes of an ordinary nature"))
            .get();
    FeedbackDTO feedbackDTO = new FeedbackDTO(
            feedback.getId(),
            feedback.getCreationDate(),
            feedback.getCreatedBy().getId(),
            null,
            null,
            feedback.getRating(),
            feedback.getNotes(),
            feedback.getRelatedTo()
    );
    UpdateFeedbackDTO updateFeedbackDTO = new UpdateFeedbackDTO(
            FeedbackRating.EXCELLENT,
            "Notes of an extraordinary nature"
    );

    @Test
    void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Feedback.class, fields::add);

        assertThat(fields.size()).isEqualTo(FeedbackDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_get_properties() {
        assertThat(feedback.getId()).isEqualTo(1L);
        assertThat(feedback.getCreatedBy()).isEqualTo(createdBy);
        assertThat(feedback.getRating()).isEqualTo(FeedbackRating.GOOD);
        assertThat(feedback.getRelatedTo()).isEqualTo("OGSM");
        assertThat(feedback.getNotes()).isEqualTo("Notes of an ordinary nature");
    }

    @Test
    void can_return_dto() { assertThat(feedback.toDto()).isEqualTo(feedbackDTO); }

}
