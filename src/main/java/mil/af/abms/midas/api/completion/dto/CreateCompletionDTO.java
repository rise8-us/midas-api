package mil.af.abms.midas.api.completion.dto;

import java.time.LocalDateTime;

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
public class CreateCompletionDTO implements MeasurableDTO, CompletableDTO {
    private String startDate;
    private String dueDate;
    private LocalDateTime completedAt;
    private CompletionType completionType;
    private Float value;
    private Float target;
    
    private Long gitlabEpicId;
    private Long gitlabIssueId;

}
