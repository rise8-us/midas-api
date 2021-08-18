package mil.af.abms.midas.api.assertion.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.AssertionStatus;
import mil.af.abms.midas.enums.AssertionType;
import mil.af.abms.midas.enums.CompletionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssertionDTO implements AbstractDTO {

    private Long id;
    private Long productId;
    private Long createdById;
    private Long parentId;

    private String text;

    private AssertionType type;
    private AssertionStatus status;

    private Set<Long> commentIds;
    private List<AssertionDTO> children;

    private LocalDateTime creationDate;
    private LocalDateTime completedDate;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;

    private Boolean isArchived;
    private CompletionType completionType;
    private Long assignedPersonId;

}
