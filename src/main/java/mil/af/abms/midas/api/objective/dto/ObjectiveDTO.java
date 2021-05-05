package mil.af.abms.midas.api.objective.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectiveDTO implements AbstractDTO {

    private Long id;
    private Long createdById;
    private Long productId;
    private String text;
    private Set<Long> assertionIds;
    private LocalDateTime creationDate;
    private LocalDateTime completedDate;

}
