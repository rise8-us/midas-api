package mil.af.abms.midas.api.feedback.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.enums.FeedbackRating;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeedbackDTO {

    @NotNull(message = "Rating must not be blank")
    private FeedbackRating rating;

    @NotNull(message = "Related to must not be blank")
    private String relatedTo;

    private String notes;
}
