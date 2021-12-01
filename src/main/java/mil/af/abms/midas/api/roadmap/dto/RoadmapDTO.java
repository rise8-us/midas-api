package mil.af.abms.midas.api.roadmap.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.RoadmapStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapDTO implements AbstractDTO {

    private Long id;
    private String title;
    private RoadmapStatus status;
    private LocalDateTime creationDate;
    private String description;
    private Boolean isHidden;
    private Long productId;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDateTime completedAt;

}
