package mil.af.abms.midas.api.measure.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.validation.AssertionExists;
import mil.af.abms.midas.enums.CompletionType;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMeasureDTO implements MeasurableDTO {
    @NotNull(message = "Value must not be blank")
    private Float value;

    @NotNull(message = "Target must not be blank")
    private Float target;

    @NotBlank(message = "Text must not be blank")
    private String text;

    @AssertionExists(allowNull = false)
    private Long assertionId;

    private ProgressionStatus status;
    private String startDate;
    private String dueDate;
    private CompletionType completionType;

}
