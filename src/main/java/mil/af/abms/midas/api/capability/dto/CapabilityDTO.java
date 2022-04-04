package mil.af.abms.midas.api.capability.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapabilityDTO implements AbstractDTO {

    private Long id;
    private LocalDateTime creationDate;
    private String title;
    private String description;
    private Set<Long> performanceMeasureIds;
    private Integer referenceId;
    private Long missionThreadId;
    private Set<Long> deliverableIds;
    private Boolean isArchived;
    private Long portfolioId;

}
