package mil.af.abms.midas.api.performancemeasure.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMeasureDTO implements AbstractDTO {

    private Long id;
    private String title;
    private LocalDateTime creationDate;
    private Set<Long> deliverableIds;
    private Integer referenceId;
    private Long capabilityId;
    private Boolean isArchived;

}
