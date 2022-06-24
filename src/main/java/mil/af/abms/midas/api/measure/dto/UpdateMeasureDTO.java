package mil.af.abms.midas.api.measure.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.completion.dto.UpdateCompletionDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMeasureDTO implements AbstractDTO {

    @NotBlank(message = "Text must not be blank")
    private String text;

    @NotNull(message = "A status must be provided")
    private ProgressionStatus status;

    private UpdateCompletionDTO completion;
}
