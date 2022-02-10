package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.dtos.AddGitLabIssueDTO;
import mil.af.abms.midas.api.issue.IssueService;
import mil.af.abms.midas.api.project.ProjectService;

public class GitLabIssueExistsValidator implements ConstraintValidator<GitLabIssueExists, AddGitLabIssueDTO> {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IssueService issueService;

    @Override
    public boolean isValid(AddGitLabIssueDTO dto, ConstraintValidatorContext constraintContext) {
        try {
            var project = projectService.findById(dto.getProjectId());
            return issueService.canAddIssue(dto.getIId(), project);
        } catch (Exception e) {
            return false;
        }
    }

}
