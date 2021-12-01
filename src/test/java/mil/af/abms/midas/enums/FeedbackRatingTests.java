package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FeedbackRatingTests {

    @Test
    void should_have_5_values() {
        assertThat(FeedbackRating.values().length).isEqualTo(5);
    }

    @Test
    void should_get_fields() {
        assertThat(FeedbackRating.UNSATISFACTORY.getName()).isEqualTo("UNSATISFACTORY");
        assertThat(FeedbackRating.UNSATISFACTORY.getDisplayName()).isEqualTo("Unsatisfactory");
        assertThat(FeedbackRating.UNSATISFACTORY.getDescription()).isEqualTo("Does not perform the task required");
        assertThat(FeedbackRating.UNSATISFACTORY.getValue()).isEqualTo(-2);
        assertThat(FeedbackRating.SATISFACTORY.getName()).isEqualTo("SATISFACTORY");
        assertThat(FeedbackRating.SATISFACTORY.getDisplayName()).isEqualTo("Satisfactory");
        assertThat(FeedbackRating.SATISFACTORY.getDescription()).isEqualTo("Needs improvement");
        assertThat(FeedbackRating.SATISFACTORY.getValue()).isEqualTo(-1);
        assertThat(FeedbackRating.AVERAGE.getName()).isEqualTo("AVERAGE");
        assertThat(FeedbackRating.AVERAGE.getDisplayName()).isEqualTo("Average");
        assertThat(FeedbackRating.AVERAGE.getDescription()).isEqualTo("Fulfills required task");
        assertThat(FeedbackRating.AVERAGE.getValue()).isEqualTo(0);
        assertThat(FeedbackRating.GOOD.getName()).isEqualTo("GOOD");
        assertThat(FeedbackRating.GOOD.getDisplayName()).isEqualTo("Good");
        assertThat(FeedbackRating.GOOD.getDescription()).isEqualTo("Works well");
        assertThat(FeedbackRating.GOOD.getValue()).isEqualTo(1);
        assertThat(FeedbackRating.EXCELLENT.getName()).isEqualTo("EXCELLENT");
        assertThat(FeedbackRating.EXCELLENT.getDisplayName()).isEqualTo("Excellent");
        assertThat(FeedbackRating.EXCELLENT.getDescription()).isEqualTo("Textbook definition of perfection");
        assertThat(FeedbackRating.EXCELLENT.getValue()).isEqualTo(2);
    }
}
