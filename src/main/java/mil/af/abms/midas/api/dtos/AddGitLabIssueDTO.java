package mil.af.abms.midas.api.dtos;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

import mil.af.abms.midas.api.validation.GitLabIssueExists;

@Data
@AllArgsConstructor
@GitLabIssueExists
public class AddGitLabIssueDTO {

    @NotNull(message = "iId cannot be null")
    private Integer iId;

    @NotNull(message = "projectId cannot be null")
    private Long projectId;

}
