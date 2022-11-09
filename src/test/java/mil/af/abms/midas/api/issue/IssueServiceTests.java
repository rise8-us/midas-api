package mil.af.abms.midas.api.issue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import mil.af.abms.midas.api.completion.CompletionService;
import mil.af.abms.midas.api.dtos.AddGitLabIssueWithProductDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabIssue;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(IssueService.class)
public class IssueServiceTests {

    @MockBean
    SimpMessageSendingOperations websocket;
    @SpyBean
    private IssueService issueService;
    @MockBean
    private IssueRepository repository;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private ProductService productService;
    @MockBean
    private CompletionService completionService;
    @MockBean
    private PortfolioService portfolioService;
    @MockBean
    private GitLab4JClient gitLab4JClient;

    @Captor
    ArgumentCaptor<Issue> captor;
    @Captor
    ArgumentCaptor<List<Issue>> listCaptor;
    @Captor
    ArgumentCaptor<Set<Issue>> setCaptor;

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
            .with(p -> p.setIsArchived(false))
            .get();
    private final Product foundProduct = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Product Name"))
            .with(p -> p.setSourceControl(sourceControl))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setProjects(Set.of(foundProject)))
            .get();
    private final Portfolio foundPortfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(10L))
            .with(p -> p.setName("Portfolio Name"))
            .with(p -> p.setSourceControl(sourceControl))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setProducts(Set.of(foundProduct)))
            .get();
    private final Issue foundIssue = Builder.build(Issue.class)
            .with(i -> i.setId(6L))
            .with(i -> i.setTitle("whoa this is issue"))
            .with(i -> i.setCreationDate(CREATED_AT))
            .with(i -> i.setIssueUid("3-42-2"))
            .with(i -> i.setIssueIid(2))
            .with(i -> i.setProject(foundProject))
            .with(i -> i.setWeight(null))
            .with(i -> i.setCompletedAt(LocalDate.parse("2022-06-15").atStartOfDay()))
            .get();
    private final Issue foundIssueNullCompleted = Builder.build(Issue.class)
            .with(i -> i.setId(6L))
            .with(i -> i.setTitle("whoa this is issue"))
            .with(i -> i.setCreationDate(CREATED_AT))
            .with(i -> i.setIssueUid("3-42-2"))
            .with(i -> i.setIssueIid(2))
            .with(i -> i.setProject(foundProject))
            .with(i -> i.setWeight(null))
            .with(i -> i.setCompletedAt(null))
            .get();
    private final GitLabIssue gitLabIssue = Builder.build(GitLabIssue.class)
            .with(i -> i.setTitle("title"))
            .with(i -> i.setIssueIid(2))
            .with(i -> i.setCreationDate(CREATED_AT))
            .with(i -> i.setCompletedAt(CLOSED_AT))
            .with(i -> i.setLabels(""))
            .get();

    @Test
    void createOrUpdate_should_create_issue_if_doesnt_exist() {
        when(projectService.findById(any())).thenReturn(foundProject);
        doReturn(gitLabIssue).when(issueService).getIssueFromClient(any(Project.class), anyInt());

        issueService.createOrUpdate(new AddGitLabIssueWithProductDTO(2, 1L));
        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getId()).isEqualTo(null);
        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getIssueUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getProject()).isEqualTo(foundProject);
    }

    @Test
    void createOrUpdate_should_update_existing_issue() {
        when(projectService.findById(any())).thenReturn(foundProject);
        doReturn(gitLabIssue).when(issueService).getIssueFromClient(any(Project.class), anyInt());
        when(repository.findByIssueUid(any())).thenReturn(Optional.of(foundIssue));

        issueService.createOrUpdate(new AddGitLabIssueWithProductDTO(2, 1L));

        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getId()).isEqualTo(6L);
        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getIssueUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getProject()).isEqualTo(foundProject);
    }

    @Test
    void updateById() {
        var issueDuplicate = new Issue();
        BeanUtils.copyProperties(foundIssue, issueDuplicate);

        when(repository.findById(any())).thenReturn(Optional.of(issueDuplicate));
        doReturn(gitLabIssue).when(issueService).getIssueFromClient(any(Project.class), anyInt());

        issueService.updateById(1L);

        verify(repository, times(1)).save(captor.capture());
        var issueSaved = captor.getValue();

        assertThat(issueSaved.getTitle()).isEqualTo("title");
    }

    @Test
    void updateById_throws_exception_if_issue_not_found() {
        assertThrows(EntityNotFoundException.class, () -> issueService.updateById(1L));
    }

    @Test
    void getAllIssuesByPortfolioId() {
        doReturn(foundPortfolio).when(portfolioService).findById(anyLong());
        doReturn(List.of(foundIssue)).when(issueService).getAllIssuesByProductId(anyLong());

        assertThat(issueService.getAllIssuesByPortfolioId(foundPortfolio.getId())).hasSize(1);
    }

    @Test
    void getAllIssuesByProductId() {
        doReturn(foundProduct).when(productService).findById(anyLong());
        doReturn(List.of(foundIssue)).when(issueService).getAllIssuesByProjectId(anyLong());

        assertThat(issueService.getAllIssuesByProductId(foundPortfolio.getId())).hasSize(1);
    }

    @Test
    void getAllIssuesByProjectId() {
        when(repository.findAllIssuesByProjectId(any())).thenReturn(Optional.of(List.of(foundIssue)));

        assertThat(issueService.getAllIssuesByProjectId(1L)).hasSize(1);
    }

    @Test
    void getAllIssuesByProjectId_should_return_empty_list_if_none_found() {
        when(repository.findAllIssuesByProjectId(any())).thenReturn(Optional.empty());

        assertThat(issueService.getAllIssuesByProjectId(1L)).hasSize(0);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "2000-01-01 : 2023-01-01 : 1",
            "2023-01-01 : 2023-01-01 : 0",
            "2000-01-01 : 2000-01-01 : 0"
    }, delimiter = ':')
    void getAllIssuesByPortfolioIdAndDateRange(String startDate, String endDate, Integer size) {
        doReturn(List.of(foundIssue, foundIssueNullCompleted)).when(issueService).getAllIssuesByPortfolioId(anyLong());

        List<Issue> foundIssues = issueService.getAllIssuesByPortfolioIdAndDateRange(1L, LocalDate.parse(startDate), LocalDate.parse(endDate));
        assertThat(foundIssues).hasSize(size);
    }

    @Test
    void gitlabIssueSync_should_save_all_issues_for_gitlab_project() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedIssue = new Issue();
        BeanUtils.copyProperties(foundIssue, expectedIssue);
        expectedIssue.setTitle(gitLabIssue.getTitle());
        expectedIssue.setId(6L);

        when(issueService.getGitlabClient(foundProject)).thenReturn(gitLab4JClient);
        doReturn(1).when(gitLab4JClient).getTotalIssuesPages(foundProject.getGitlabProjectId());
        doReturn(Set.of(expectedIssue)).when(issueService).processIssues(List.of(), foundProject);

        issueService.gitlabIssueSync(foundProject);
        verify(repository, times(1)).saveAll(setCaptor.capture());
        Set<Issue> savedIssues = setCaptor.getValue();
        assertThat(savedIssues).hasSize(1);
        assertThat(savedIssues).isEqualTo(Set.of(expectedIssue));
    }

    @Test
    void syncGitlabIssueForProduct() {
        when(productService.findById(anyLong())).thenReturn(foundProduct);
        doReturn(List.of(foundIssue)).when(issueService).gitlabIssueSync(any(Project.class));

        assertThat(issueService.syncGitlabIssueForProduct(2L)).isEqualTo(Set.of((foundIssue)));
    }

    @Test
    void gitlabIssueSync_should_return_empty_list_when_project_is_archived() {
        foundProject.setIsArchived(true);

        assertThat(issueService.gitlabIssueSync(foundProject)).isEqualTo(List.of());
    }

    @Test
    void gitlabIssueSync_should_return_empty_list_when_project_gitlab_id_is_null() {
        foundProject.setGitlabProjectId(null);

        assertThat(issueService.gitlabIssueSync(foundProject)).isEqualTo(List.of());
    }

    @Test
    void gitlabIssueSync_should_return_empty_list_when_project_source_control_is_null() {
        foundProject.setSourceControl(null);

        assertThat(issueService.gitlabIssueSync(foundProject)).isEqualTo(List.of());
    }

    @Test
    void gitlabIssueSync_should_return_empty_list_when_project_source_control_token_is_null() {
        sourceControl.setToken(null);
        foundProject.setSourceControl(sourceControl);

        assertThat(issueService.gitlabIssueSync(foundProject)).isEqualTo(List.of());
    }

    @Test
    void gitlabIssueSync_should_return_empty_list_when_project_source_control_base_url_is_null() {
        sourceControl.setBaseUrl(null);
        foundProject.setSourceControl(sourceControl);

        assertThat(issueService.gitlabIssueSync(foundProject)).isEqualTo(List.of());
    }

    @Test
    void runScheduledIssueSync_should_sync_issues_for_all_unarchived_projects() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedIssue = new Issue();
        BeanUtils.copyProperties(gitLabIssue, expectedIssue);
        expectedIssue.setCreationDate(CREATED_AT);

        var archivedProject = new Project();
        BeanUtils.copyProperties(foundProject, archivedProject);
        archivedProject.setIsArchived(true);

        doReturn(List.of(foundProject, archivedProject)).when(projectService).getAll();
        doReturn(gitLab4JClient).when(issueService).getGitlabClient(any());

        issueService.runScheduledIssueSync();

        verify(issueService, times(1)).gitlabIssueSync(foundProject);
    }

    @Test
    void canAddIssue_returns_false_if_issue_not_found() {
        when(gitLab4JClient.findIssueByIdAndProjectId(any(), any())).thenReturn(Optional.empty());
        assertFalse(issueService.canAddIssue(foundIssue.getIssueIid(), foundProject));
    }

    @Test
    void removeAllUntrackedIssues_should_delete_all_untracked_issues() {
        Issue issueToKeep = new Issue();
        Issue issueToRemove = new Issue();
        BeanUtils.copyProperties(foundIssue, issueToKeep);
        BeanUtils.copyProperties(foundIssue, issueToRemove);
        issueToKeep.setTitle(gitLabIssue.getTitle());
        issueToKeep.setIssueIid(2);
        issueToRemove.setIssueIid(43);

        doReturn(new ArrayList<>(List.of(issueToKeep, issueToRemove))).when(issueService).getAllIssuesByProjectId(foundProject.getId());

        issueService.removeAllUntrackedIssues(foundProject.getId(), Set.of(foundIssue));

        verify(repository, times(1)).deleteAll(listCaptor.capture());
        List<Issue> issuesDeleted = listCaptor.getValue();
        assertThat(issuesDeleted).hasSize(1);
        assertThat(issuesDeleted).isEqualTo(new ArrayList<>(List.of(issueToRemove)));
    }

    @Test
    void getIssueFromClient() {
        doReturn(gitLabIssue).when(gitLab4JClient).getIssueFromProject(anyInt(), anyInt());
        doReturn(gitLab4JClient).when(issueService).getGitlabClient(any());

        assertThat(issueService.getIssueFromClient(foundProject, 2)).isEqualTo(gitLabIssue);
    }

    @Test
    void filterCompletedAtByDateRange() {
        Issue beforeIssue = new Issue();
        Issue afterIssue = new Issue();
        Issue incompleteIssue = new Issue();
        Issue inRangeIssue = new Issue();
        BeanUtils.copyProperties(foundIssue, beforeIssue);
        BeanUtils.copyProperties(foundIssue, afterIssue);
        BeanUtils.copyProperties(foundIssue, incompleteIssue);
        BeanUtils.copyProperties(foundIssue, inRangeIssue);

        beforeIssue.setCompletedAt(LocalDate.parse("2022-06-01").atStartOfDay());
        afterIssue.setCompletedAt(LocalDate.parse("2022-06-25").atStartOfDay());
        incompleteIssue.setCompletedAt(null);

        LocalDateTime startDate = LocalDate.parse("2022-06-05").atStartOfDay();
        LocalDateTime endDate = LocalDate.parse("2022-06-20").atStartOfDay();

        assertThat(issueService.filterCompletedAtByDateRange(List.of(inRangeIssue, beforeIssue, afterIssue, incompleteIssue), startDate, endDate)).hasSize(1);
    }

    @Test
    void should_get_issues_by_project_id_and_date_range() {
        doReturn(Optional.of(List.of(foundIssue))).when(repository).findAllIssuesByProjectIdAndCompletedAtDateRange(anyLong(), any(), any());
        List<Issue> foundIssues = issueService.findAllIssuesByProjectIdAndCompletedAtDateRange(
                1L,
                LocalDate.parse("2022-01-01").atStartOfDay(),
                LocalDateTime.now()
        );

        assertThat(foundIssues).hasSize(1);
    }

    @Test
    void should_get_no_issues_by_project_id_and_date_range() {
        doReturn(Optional.of(List.of())).when(repository).findAllIssuesByProjectIdAndCompletedAtDateRange(anyLong(), any(), any());
        List<Issue> noIssuesFound = issueService.findAllIssuesByProjectIdAndCompletedAtDateRange(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assertThat(noIssuesFound).hasSize(0);
    }

}
