package mil.af.abms.midas.api.measure.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.completion.dto.CreateCompletionDTO;
import mil.af.abms.midas.api.validation.AssertionExists;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMeasureDTO {

    @NotBlank(message = "Text must not be blank")
    private String text;

    @AssertionExists(allowNull = false)
    private Long assertionId;

    private ProgressionStatus status;
    
    private CreateCompletionDTO completion;

}
