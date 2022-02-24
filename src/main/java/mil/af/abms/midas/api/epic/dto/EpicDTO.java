package mil.af.abms.midas.api.epic.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EpicDTO implements AbstractDTO {

    private Long id;
    private String title;
    private String description;
    private Boolean isHidden;
    private LocalDateTime creationDate;
    private LocalDate startDate;
    private LocalDate startDateFromInheritedSource;
    private LocalDate dueDate;
    private LocalDate dueDateFromInheritedSource;
    private LocalDateTime closedAt;
    private LocalDateTime syncedAt;
    private Integer epicIid;
    private String state;
    private String webUrl;
    private String epicUid;
    private Long totalWeight;
    private Long completedWeight;
    private Long productId;

}
