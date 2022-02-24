package mil.af.abms.midas.api.measure.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.completion.dto.CompletionDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasureDTO implements AbstractDTO {

    private Long id;
    private LocalDateTime creationDate;
    private String text;
    private Long assertionId;
    private Set<Long> commentIds;
    private ProgressionStatus status;
    private CompletionDTO completion;

}
