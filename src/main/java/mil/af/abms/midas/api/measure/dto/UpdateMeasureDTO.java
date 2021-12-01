package mil.af.abms.midas.api.measure.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.enums.CompletionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMeasureDTO {

    @NotNull(message = "Value must not be blank")
    private Float value;

    @NotNull(message = "Target must not be blank")
    private Float target;

    @NotBlank(message = "Text must not be blank")
    private String text;

    private String startDate;
    private String dueDate;
    private CompletionType completionType;

}
