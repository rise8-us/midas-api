package mil.af.abms.midas.api.completion.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.api.issue.dto.IssueDTO;
import mil.af.abms.midas.enums.CompletionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompletionDTO implements AbstractDTO {

        private Long id;
        private LocalDate startDate;
        private LocalDate dueDate;
        private LocalDateTime completedAt;
        private CompletionType completionType;
        private Float value;
        private Float target;

        private EpicDTO gitlabEpic;
        private IssueDTO gitlabIssue;
}
