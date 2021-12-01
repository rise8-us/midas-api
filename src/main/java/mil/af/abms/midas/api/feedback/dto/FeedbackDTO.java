package mil.af.abms.midas.api.feedback.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.FeedbackRating;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO implements AbstractDTO {

    private Long id;
    private LocalDateTime creationDate;
    private Long createdById;
    private Long editedById;
    private LocalDateTime editedAt;
    private FeedbackRating rating;
    private String notes;
    private String relatedTo;

}
