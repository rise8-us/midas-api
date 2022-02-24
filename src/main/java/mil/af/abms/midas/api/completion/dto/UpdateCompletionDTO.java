package mil.af.abms.midas.api.completion.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.dtos.CompletableDTO;
import mil.af.abms.midas.api.measure.dto.MeasurableDTO;
import mil.af.abms.midas.api.validation.IsValidDueDate;
import mil.af.abms.midas.enums.CompletionType;

@Data
@IsValidDueDate
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompletionDTO implements MeasurableDTO, CompletableDTO {

    private String startDate;
    private String dueDate;

    private CompletionType completionType;
    
    @NotNull(message = "Value must not be blank")
    private Float value;
    
    @NotNull(message = "Target must not be blank")
    private Float target;

    private Long gitlabEpicId;
    private Long gitlabIssueId;
}
