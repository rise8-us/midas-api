package mil.af.abms.midas.api.issue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.dtos.AddGitLabIssueDTO;
import mil.af.abms.midas.api.issue.dto.IssueDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabIssue;

@Slf4j
@Service
public class IssueService extends AbstractCRUDService<Issue, IssueDTO, IssueRepository> {

    private ProjectService projectService;

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public IssueService(IssueRepository repository) {
        super(repository, Issue.class, IssueDTO.class);
    }

    public Issue create(AddGitLabIssueDTO dto) {
        var project = projectService.findById(dto.getProjectId());
        var sourceControlId = project.getSourceControl().getId();
        var gitlabProjectId = project.getGitlabProjectId();
        var issueConversion = getIssueFromClient(project, dto.getIId());
        var uId = generateUniqueId(sourceControlId, gitlabProjectId, dto.getIId());

        return repository.findByIssueUid(uId)
                .map(issue -> syncIssue(issueConversion, issue))
                .orElseGet(() -> convertToIssue(issueConversion, project));

    }

    public Issue updateById(Long id) {
        var foundIssue = findById(id);
        var project = foundIssue.getProject();
        var gitLabIssue = getIssueFromClient(project, foundIssue.getIssueIid());

        return syncIssue(gitLabIssue, foundIssue);
    }

    public void removeAllUntrackedIssues(Long projectId, List<GitLabIssue> gitLabIssueList) {
        var issueIids = gitLabIssueList.stream().map(GitLabIssue::getIssueIid).collect(Collectors.toSet());
        var midasProjectIssues = getAllGitlabIssuesForProject(projectId);
        var midasProjectIssuesIids = midasProjectIssues.stream().map(Issue::getIssueIid).collect(Collectors.toSet());

        midasProjectIssuesIids.removeAll(issueIids);
        midasProjectIssues.removeIf(issue -> !midasProjectIssuesIids.contains(issue.getIssueIid()));
        repository.deleteAll(midasProjectIssues);
    }

    public Set<Issue> getAllGitlabIssuesForProject(Long projectId) {
        var project = projectService.findById(projectId);
        if (hasGitlabDetails(project)) {
            var allIssuesInGitLab = getGitlabClient(project)
                    .getIssuesFromProject(project.getGitlabProjectId());
            var sourceControlId = project.getSourceControl().getId();
            var gitlabProjectId = project.getGitlabProjectId();

            removeAllUntrackedIssues(projectId, allIssuesInGitLab);

            return allIssuesInGitLab.stream()
                    .map(i ->
                            repository.findByIssueUid(generateUniqueId(sourceControlId, gitlabProjectId, i.getIssueIid()))
                                    .map(issue -> syncIssue(i, issue))
                                    .orElseGet(() -> convertToIssue(i, project))
                    ).collect(Collectors.toSet());
        }

        return Set.of();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledIssueSync() {
        for (Project project : projectService.getAll()) {
            if (project.getIsArchived() == Boolean.FALSE) {
                getAllGitlabIssuesForProject(project.getId());
            }
        }
    }

    public boolean canAddIssue(Integer iid, Project project) {
        var client = getGitlabClient(project);
        return client.issueExistsByIdAndProjectId(iid, project.getGitlabProjectId());
    }

    protected GitLab4JClient getGitlabClient(Project project) {
        return new GitLab4JClient(project.getSourceControl());
    }

    protected Issue convertToIssue(GitLabIssue gitLabIssue, Project project) {
        var sourceControlId = project.getSourceControl().getId();
        var gitlabProjectId = project.getGitlabProjectId();
        var uId = generateUniqueId(sourceControlId, gitlabProjectId, gitLabIssue.getIssueIid());
        var newIssue = new Issue();
        BeanUtils.copyProperties(gitLabIssue, newIssue);
        newIssue.setSyncedAt(LocalDateTime.now());
        newIssue.setIssueUid(uId);
        newIssue.setProject(project);
        return repository.save(newIssue);
    }

    protected String generateUniqueId(Long sourceControlId, Integer gitlabProjectId, Integer issueIid) {
        return String.format("%d-%d-%d", sourceControlId, gitlabProjectId, issueIid);
    }

    protected GitLabIssue getIssueFromClient(Project project, Integer issueIid) {
        var client = getGitlabClient(project);
        return client.getIssueFromProject(project.getGitlabProjectId(), issueIid);
    }

    protected Issue syncIssue(GitLabIssue gitLabIssue, Issue issue) {
        BeanUtils.copyProperties(gitLabIssue, issue);
        issue.setSyncedAt(LocalDateTime.now());
        return repository.save(issue);
    }

    private boolean hasGitlabDetails(Project project) {
        return !project.getIsArchived() &&
                project.getGitlabProjectId() != null &&
                project.getSourceControl() != null &&
                project.getSourceControl().getToken() != null &&
                project.getSourceControl().getBaseUrl() != null;
    }

}
