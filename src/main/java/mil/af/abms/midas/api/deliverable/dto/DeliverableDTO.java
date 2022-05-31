package mil.af.abms.midas.api.deliverable.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.completion.dto.CompletionDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliverableDTO implements AbstractDTO {

    private Long id;
    private String title;
    private LocalDateTime creationDate;
    private ProgressionStatus status;
    private Integer index;
    private Integer referenceId;
    private Set<Long> releaseIds;
    private List<DeliverableDTO> children;
    private Long parentId;
    private Long productId;
    private Set<Long> targetIds;
    private Long performanceMeasureId;
    private Long capabilityId;
    private Long assignedToId;
    private Boolean isArchived;
    private CompletionDTO completion;

}
