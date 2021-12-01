package mil.af.abms.midas.api.assertion.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssertionDTO implements AbstractDTO {

    private Long id;
    private Long productId;
    private Long createdById;
    private Long parentId;
    private Long inheritedFromId;

    private String text;

    private ProgressionStatus status;

    private Set<Long> commentIds;
    private List<Long> measureIds;
    private List<AssertionDTO> children;
    private List<Long> passedToIds;

    private LocalDateTime creationDate;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDateTime completedAt;

    private Boolean isArchived;
    private Long assignedPersonId;

}
