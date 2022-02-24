package mil.af.abms.midas.api.issue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import mil.af.abms.midas.api.dtos.AddGitLabIssueDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabIssue;

@ExtendWith(SpringExtension.class)
@Import(IssueService.class)
public class IssueServiceTests {

    @SpyBean
    private IssueService issueService;
    @MockBean
    private IssueRepository repository;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private  GitLab4JClient gitLab4JClient;

    @Captor
    ArgumentCaptor<Issue> captor;

    private static final LocalDateTime CREATED_AT = LocalDateTime.now().minusDays(1L);
    private static final LocalDateTime CLOSED_AT = LocalDateTime.now();

    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(sc -> sc.setId(3L))
            .with(sc -> sc.setToken("fake_token"))
            .with(sc -> sc.setBaseUrl("fake_url"))
            .get();
    private final Project foundProject = Builder.build(Project.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setGitlabProjectId(42))
            .with(p -> p.setSourceControl(sourceControl))
            .get();
    private final Issue foundIssue = Builder.build(Issue.class)
            .with(i -> i.setId(6L))
            .with(i -> i.setTitle("whoa this is issue"))
            .with(i -> i.setCreationDate(CREATED_AT))
            .with(i -> i.setIssueUid("3422"))
            .with(i -> i.setIssueIid(2))
            .with(i -> i.setProject(foundProject))
            .get();
    private final GitLabIssue gitLabIssue = Builder.build(GitLabIssue.class)
            .with(i -> i.setTitle("title"))
            .with(i -> i.setIssueIid(2))
            .with(i -> i.setCreationDate(CREATED_AT))
            .with(i -> i.setCompletedAt(CLOSED_AT))
            .get();

    @Test
    void can_create_Epic_new() {
        doReturn(gitLabIssue).when(issueService).getIssueFromClient(any(Project.class), anyInt());
        when(projectService.findById(any())).thenReturn(foundProject);

        issueService.create(new AddGitLabIssueDTO(2, 1L));
        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getIssueUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getProject()).isEqualTo(foundProject);
    }

    @Test
    void can_create_Epic_exists() {

        doReturn(gitLabIssue).when(issueService).getIssueFromClient(any(Project.class), anyInt());
        when(projectService.findById(any())).thenReturn(foundProject);
        when(repository.findByIssueUid(foundIssue.getIssueUid())).thenReturn(Optional.of(foundIssue));

        issueService.create(new AddGitLabIssueDTO(2, 1L));

        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getIssueUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getProject()).isEqualTo(foundProject);
    }

    @Test
    void can_update_issue() {
        var issueDuplicate = new Issue();
        BeanUtils.copyProperties(foundIssue, issueDuplicate);

        doReturn(gitLabIssue).when(issueService).getIssueFromClient(any(Project.class), anyInt());
        when(projectService.findById(any())).thenReturn(foundProject);
        when(repository.findById(any())).thenReturn(Optional.of(issueDuplicate));

        issueService.updateById(1L);

        verify(repository, times(1)).save(captor.capture());
        var issueSaved = captor.getValue();

        assertThat(issueSaved.getTitle()).isNotEqualTo(foundIssue.getTitle());
        assertThat(issueSaved.getTitle()).isEqualTo("title");
    }

    @Test
    void should_get_all_issues_for_gitlab_project() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedIssue = new Issue();
        BeanUtils.copyProperties(foundIssue, expectedIssue);
        expectedIssue.setTitle(gitLabIssue.getTitle());
        expectedIssue.setId(6L);

        doReturn(expectedIssue).when(repository).save(any(Issue.class));
        when(issueService.getGitlabClient(foundProject)).thenReturn(gitLab4JClient);
        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);
        doReturn(Optional.of(foundIssue)).when(repository).findByIssueUid(any());
        doNothing().when(issueService).removeAllUntrackedIssues(foundProject.getId(), List.of(gitLabIssue));
        when(gitLab4JClient.getIssuesFromProject(foundProject.getGitlabProjectId())).thenReturn(List.of(gitLabIssue));

        assertThat(issueService.getAllGitlabIssuesForProject(foundProject.getId())).isEqualTo(Set.of(expectedIssue));
    }

    @Test
    void should_get_all_issues_for_gitlab_project_when_unable_to_findByIssueUid() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedIssue = new Issue();
        BeanUtils.copyProperties(foundIssue, expectedIssue);
        expectedIssue.setTitle(gitLabIssue.getTitle());
        expectedIssue.setId(6L);

        doReturn(expectedIssue).when(repository).save(any(Issue.class));
        when(issueService.getGitlabClient(foundProject)).thenReturn(gitLab4JClient);
        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);
        doReturn(Optional.empty()).when(repository).findByIssueUid(any());
        doNothing().when(issueService).removeAllUntrackedIssues(foundProject.getId(), List.of(gitLabIssue));
        when(gitLab4JClient.getIssuesFromProject(foundProject.getGitlabProjectId())).thenReturn(List.of(gitLabIssue));

        assertThat(issueService.getAllGitlabIssuesForProject(foundProject.getId())).isEqualTo(Set.of(expectedIssue));
    }

    @Test
    void should_return_empty_set_when_project_is_archived() {
        foundProject.setIsArchived(true);

        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);

        assertThat(issueService.getAllGitlabIssuesForProject(foundProject.getId())).isEqualTo(Set.of());
    }

    @Test
    void should_return_empty_set_when_project_gitlab_id_is_null() {
        foundProject.setGitlabProjectId(null);

        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);

        assertThat(issueService.getAllGitlabIssuesForProject(foundProject.getId())).isEqualTo(Set.of());
    }

    @Test
    void should_return_empty_set_when_project_source_control_is_null() {
        foundProject.setSourceControl(null);

        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);

        assertThat(issueService.getAllGitlabIssuesForProject(foundProject.getId())).isEqualTo(Set.of());
    }

    @Test
    void should_return_empty_set_when_project_source_control_token_is_null() {
        sourceControl.setToken(null);
        foundProject.setSourceControl(sourceControl);

        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);

        assertThat(issueService.getAllGitlabIssuesForProject(foundProject.getId())).isEqualTo(Set.of());
    }

    @Test
    void should_return_empty_set_when_project_source_control_base_url_is_null() {
        sourceControl.setBaseUrl(null);
        foundProject.setSourceControl(sourceControl);

        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);

        assertThat(issueService.getAllGitlabIssuesForProject(foundProject.getId())).isEqualTo(Set.of());
    }

    @Test
    void should_run_scheduled_issue_sync() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedIssue = new Issue();
        BeanUtils.copyProperties(gitLabIssue, expectedIssue);
        expectedIssue.setCreationDate(CREATED_AT);

        var archivedProject = new Project();
        BeanUtils.copyProperties(foundProject, archivedProject);
        archivedProject.setIsArchived(true);

        doReturn(List.of(foundProject.getId())).when(projectService).getAll();
        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);
        when(projectService.getAll()).thenReturn(List.of(foundProject, archivedProject));
        doReturn(gitLab4JClient).when(issueService).getGitlabClient(any());
        doNothing().when(issueService).removeAllUntrackedIssues(foundProject.getId(), List.of(gitLabIssue));
        when(gitLab4JClient.getIssuesFromProject(foundProject.getGitlabProjectId())).thenReturn(List.of(gitLabIssue));

        issueService.runScheduledIssueSync();

        verify(repository, times(1)).save(captor.capture());
        Issue issueSaved = captor.getValue();
        issueSaved.setCreationDate(CREATED_AT);

        assertThat(issueSaved).isEqualTo(expectedIssue);
        assertThat(gitLabIssue.getCompletedAt()).isEqualTo(issueSaved.getCompletedAt());
    }

    @Test
    void can_add_Issue_returns_false() {
        assertFalse(issueService.canAddIssue(foundIssue.getIssueIid(), foundProject));
    }

    @Test
    void should_remove_all_untracked_issues() {

        var expectedIssue = new Issue();
        BeanUtils.copyProperties(foundIssue, expectedIssue);
        expectedIssue.setTitle(gitLabIssue.getTitle());
        expectedIssue.setIssueIid(42);

        doReturn(new HashSet<>(Stream.of(foundIssue, expectedIssue).collect(Collectors.toSet()))).when(issueService).getAllGitlabIssuesForProject(anyLong());
        doNothing().when(repository).deleteAll(anyList());

        issueService.removeAllUntrackedIssues(foundProject.getId(), List.of(gitLabIssue));

        verify(repository, times(1)).deleteAll(any());

    }

    @Test
    void should_get_issue_from_client() {
        doReturn(gitLabIssue).when(gitLab4JClient).getIssueFromProject(anyInt(), anyInt());
        doReturn(gitLab4JClient).when(issueService).getGitlabClient(any());

        issueService.getIssueFromClient(foundProject, 2);

        verify(issueService, times(1)).getIssueFromClient(any(), anyInt());
    }

}
