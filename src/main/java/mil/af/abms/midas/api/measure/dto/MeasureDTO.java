package mil.af.abms.midas.api.measure.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.CompletionType;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasureDTO implements AbstractDTO {

    private Long id;
    private LocalDateTime creationDate;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDateTime completedAt;
    private CompletionType completionType;
    private Float value;
    private Float target;
    private String text;
    private Long assertionId;
    private Set<Long> commentIds;
    private ProgressionStatus status;

}
