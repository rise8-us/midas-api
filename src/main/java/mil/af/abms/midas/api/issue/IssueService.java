package mil.af.abms.midas.api.issue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.completion.CompletionService;
import mil.af.abms.midas.api.dtos.AddGitLabIssueWithProductDTO;
import mil.af.abms.midas.api.dtos.PaginationProgressDTO;
import mil.af.abms.midas.api.issue.dto.IssueDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabIssue;
import mil.af.abms.midas.enums.SyncStatus;

@Slf4j
@Service
public class IssueService extends AbstractCRUDService<Issue, IssueDTO, IssueRepository> {

    private CompletionService completionService;
    private ProductService productService;
    private ProjectService projectService;
    private PortfolioService portfolioService;
    private final SimpMessageSendingOperations websocket;

    @Autowired
    public void setCompletionService(CompletionService completionService) { this.completionService = completionService; }
    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }
    @Autowired
    public void setProjectService(ProjectService projectService) { this.projectService = projectService; }
    @Autowired
    public void setPortfolioService(PortfolioService portfolioService) { this.portfolioService = portfolioService; }

    public IssueService(IssueRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Issue.class, IssueDTO.class);
        this.websocket = websocket;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledIssueSync() {
        for (Project project : projectService.getAll()) {
            if (project.getIsArchived() == Boolean.FALSE) {
                gitlabIssueSync(project);
            }
        }
    }

    public List<Issue> getAllIssuesByPortfolioId(Long portfolioId) {
        Portfolio portfolio = portfolioService.findById(portfolioId);
        return portfolio.getProducts().stream()
                .flatMap(p -> getAllIssuesByProductId(p.getId()).stream()).collect(Collectors.toList());
    }

    public List<Issue> getAllIssuesByProductId(Long productId) {
        Product product = productService.findById(productId);
        return product.getProjects().stream()
                .flatMap(p -> getAllIssuesByProjectId(p.getId()).stream()).collect(Collectors.toList());
    }

    public List<Issue> getAllIssuesByProjectId(Long projectId) {
        return repository.findAllIssuesByProjectId(projectId).orElse(List.of());
    }

    public Set<Issue> syncGitlabIssueForProduct(Long productId) {
        Product product = productService.findById(productId);
        return product.getProjects().stream()
                .flatMap(p -> gitlabIssueSync(p).stream()).collect(Collectors.toSet());
    }

    public Issue create(AddGitLabIssueWithProductDTO dto) {
        Project project = projectService.findById(dto.getProjectId());
        Long sourceControlId = project.getSourceControl().getId();
        Integer gitlabProjectId = project.getGitlabProjectId();
        GitLabIssue issueConversion = getIssueFromClient(project, dto.getIId());
        String uId = generateUniqueId(sourceControlId, gitlabProjectId, dto.getIId());

        return repository.findByIssueUid(uId)
                .map(issue -> syncIssue(issueConversion, issue))
                .orElseGet(() -> convertToIssue(issueConversion, project));

    }

    public Set<Issue> syncGitlabIssueForProject(Long projectId) {
        Project project = projectService.findById(projectId);
        return gitlabIssueSync(project);
    }

    public Issue updateById(Long id) {
        Issue foundIssue = findById(id);
        Project project = foundIssue.getProject();
        return syncOrDeleteIssue(foundIssue, getIssueFromClient(project, foundIssue.getIssueIid()));
    }

    private Issue syncOrDeleteIssue(Issue foundIssue, GitLabIssue issueFromClient) {
        try {
            return syncIssue(issueFromClient, foundIssue);
        } catch (Exception e) {
            foundIssue.getCompletions().forEach(c -> completionService.setCompletionTypeToFailure(c.getId()));

            repository.delete(foundIssue);
            return null;
        }
    }

    public void removeAllUntrackedIssues(Long projectId, Set<Issue> fetchedIssueSet) {
        Set<Integer> issueIids = fetchedIssueSet.stream().map(Issue::getIssueIid).collect(Collectors.toSet());
        List<Issue> midasProjectIssues = getAllIssuesByProjectId(projectId);
        Set<Integer> midasProjectIssuesIids = midasProjectIssues.stream().map(Issue::getIssueIid).collect(Collectors.toSet());

        midasProjectIssuesIids.removeAll(issueIids);
        midasProjectIssues.removeIf(issue -> !midasProjectIssuesIids.contains(issue.getIssueIid()));
        midasProjectIssues.forEach((this::updateCompletionType));

        repository.deleteAll(midasProjectIssues);
    }

    private void updateCompletionType(Issue issue) {
        Optional.ofNullable(issue.getCompletions()).ifPresent(completions -> completions.forEach(c ->
                completionService.setCompletionTypeToFailure(c.getId())
        ));
    }

    public Set<Issue> gitlabIssueSync(Project project) {
        if (!hasGitlabDetails(project)) { return Set.of(); }

        PaginationProgressDTO paginationProgressDTO = new PaginationProgressDTO();
        paginationProgressDTO.setId(project.getId());

        GitLab4JClient client = getGitlabClient(project);
        int totalPageCount = client.getTotalIssuesPages(project.getGitlabProjectId());

        Set<Issue> allIssues = new HashSet<>();
        for (int i = 1; i <= totalPageCount; i++) {
            allIssues.addAll(processIssues(client.fetchGitLabIssueByPage(project, i), project));
            paginationProgressDTO.setValue((double) i / totalPageCount);

            if (i == totalPageCount) { paginationProgressDTO.setStatus(SyncStatus.SYNCED); }
            websocket.convertAndSend("/topic/fetchGitlabIssuesPagination", paginationProgressDTO);
        }

        removeAllUntrackedIssues(project.getId(), allIssues);

        return allIssues;
    }

    public Set<Issue> processIssues(List<GitLabIssue> issues, Project project) {
        Long sourceControlId = project.getSourceControl().getId();
        Integer gitlabProjectId = project.getGitlabProjectId();

        return issues.stream().map(i ->
                repository.findByIssueUid(generateUniqueId(sourceControlId, gitlabProjectId, i.getIssueIid()))
                        .map(issue -> syncIssue(i, issue))
                        .orElseGet(() -> convertToIssue(i, project))
        ).collect(Collectors.toSet());
    }

    public boolean canAddIssue(Integer iid, Project project) {
        GitLab4JClient client = getGitlabClient(project);
        return client.issueExistsByIdAndProjectId(iid, project.getGitlabProjectId());
    }

    protected String generateUniqueId(Long sourceControlId, Integer gitlabProjectId, Integer issueIid) {
        return String.format("%d-%d-%d", sourceControlId, gitlabProjectId, issueIid);
    }

    protected GitLabIssue getIssueFromClient(Project project, Integer issueIid) {
        GitLab4JClient client = getGitlabClient(project);
        return client.getIssueFromProject(project.getGitlabProjectId(), issueIid);
    }

    protected GitLab4JClient getGitlabClient(Project project) {
        return new GitLab4JClient(project.getSourceControl());
    }

    protected Issue convertToIssue(GitLabIssue gitLabIssue, Project project) {
        String uId = generateUniqueId(project.getSourceControl().getId(), project.getGitlabProjectId(), gitLabIssue.getIssueIid());
        Issue newIssue = new Issue();
        BeanUtils.copyProperties(gitLabIssue, newIssue);
        newIssue.setSyncedAt(LocalDateTime.now());
        newIssue.setIssueUid(uId);
        newIssue.setProject(project);

        Issue updatedIssue = setWeight(newIssue);

        return repository.save(updatedIssue);
    }

    protected Issue syncIssue(GitLabIssue gitLabIssue, Issue issue) {
        BeanUtils.copyProperties(gitLabIssue, issue);

        Issue updatedIssue = setWeight(issue);
        completionService.updateLinkedIssue(updatedIssue);
        updatedIssue.setSyncedAt(LocalDateTime.now());

        return repository.save(updatedIssue);
    }

    protected boolean hasGitlabDetails(Project project) {
        return !project.getIsArchived() &&
                project.getGitlabProjectId() != null &&
                project.getSourceControl() != null &&
                project.getSourceControl().getToken() != null &&
                project.getSourceControl().getBaseUrl() != null;
    }

    protected Issue setWeight(Issue issue) {
        if (issue.getWeight() == null) {
            issue.setWeight(0L);
        }

        return issue;
    }

    public List<Issue> filterCompletedAtByDateRange(List<Issue> issues, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return issues.stream().filter(i ->
                Optional.ofNullable(i.getCompletedAt()).isPresent() &&
                        i.getCompletedAt().isAfter(startDateTime) &&
                        i.getCompletedAt().isBefore(endDateTime)
        ).collect(Collectors.toList());
    }

    public List<Issue> findAllIssuesByProjectIdAndCompletedAtDateRange(Long projectId, LocalDateTime startDate, LocalDateTime endDate) {
        Optional<List<Issue>> filteredIssues = repository.findAllIssuesByProjectIdAndCompletedAtDateRange(projectId, startDate, endDate);
        return filteredIssues.orElseGet(List::of);
    }

}
