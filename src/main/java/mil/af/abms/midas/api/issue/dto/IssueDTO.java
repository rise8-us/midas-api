package mil.af.abms.midas.api.issue.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueDTO implements AbstractDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime syncedAt;
    private Integer issueIid;
    private String issueUid;
    private String state;
    private String webUrl;
    private Long weight;
    private Long projectId;
    private List<String> labels;
}
