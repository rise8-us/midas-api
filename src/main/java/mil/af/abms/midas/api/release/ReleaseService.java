package mil.af.abms.midas.api.release;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.release.dto.ReleaseDTO;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabRelease;
import mil.af.abms.midas.enums.SyncStatus;

@Service
public class ReleaseService extends AbstractCRUDService<Release, ReleaseDTO, ReleaseRepository> {

    private ProjectService projectService;
    private ProductService productService;

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }

    public ReleaseService(ReleaseRepository repository) {
        super(repository, Release.class, ReleaseDTO.class);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledReleaseSync() {
        for (Project project : projectService.getAll()) {
            if (project.getIsArchived() == Boolean.FALSE) {
                gitlabReleaseSync(project);
            }
        }
    }

    public List<Release> getAllReleasesByProductId(Long productId) {
        Product product = productService.findById(productId);
        List<Release> releases = new ArrayList<>();
        product.getProjects().forEach(p -> releases.addAll(getAllReleasesByProjectId(p.getId())));
        return releases;
    }

    public Set<Release> syncGitlabReleaseForProduct(Long productId) {
        Product product = productService.findById(productId);
        Set<Release> releases = new HashSet<>();
        product.getProjects().forEach(p -> releases.addAll(gitlabReleaseSync(p)));
        return releases;
    }

    public List<Release> getAllReleasesByProjectId(Long projectId) {
        return repository.findAllReleasesByProjectId(projectId).orElse(List.of());
    }

    public Set<Release> syncGitlabReleaseForProject(Long projectId) {
        Project project = projectService.findById(projectId);
        return gitlabReleaseSync(project);
    }

    public Set<Release> gitlabReleaseSync(Project project) {
        if (!hasGitlabDetails(project)) { return Set.of(); }

        projectService.updateReleaseSyncStatus(project.getId(), SyncStatus.SYNCING);

        GitLab4JClient client = getGitlabClient(project);
        int totalPageCount = client.getTotalReleasesPages(project.getGitlabProjectId());
        Set<Release> allReleases = new HashSet<>();

        for (int i = 1; i <= totalPageCount; i++) {
            List<GitLabRelease> pagedGitlabReleases = client.fetchGitLabReleasesByPage(project.getGitlabProjectId(), i);
            Set<Release> processedReleases = processReleases(pagedGitlabReleases, project);
            allReleases.addAll(processedReleases);
        }

        removeAllUntrackedReleases(project.getId(), allReleases);

        projectService.updateReleaseSyncStatus(project.getId(), SyncStatus.SYNCED);

        return allReleases;
    }

    public void removeAllUntrackedReleases(Long projectId, Set<Release> fetchedReleaseSet) {
        Set<String> releaseNames = fetchedReleaseSet.stream().map(Release::getName).collect(Collectors.toSet());
        List<Release> midasProjectReleases = getAllReleasesByProjectId(projectId);
        Set<String> midasProjectReleasesNames = midasProjectReleases.stream().map(Release::getName).collect(Collectors.toSet());

        midasProjectReleasesNames.removeAll(releaseNames);
        midasProjectReleases.removeIf(release -> !midasProjectReleasesNames.contains(release.getName()));

        repository.deleteAll(midasProjectReleases);
    }

    protected boolean hasGitlabDetails(Project project) {
        return !project.getIsArchived() &&
                project.getGitlabProjectId() != null &&
                project.getSourceControl() != null &&
                project.getSourceControl().getToken() != null &&
                project.getSourceControl().getBaseUrl() != null;
    }

    public Set<Release> processReleases(List<GitLabRelease> releases, Project project) {
        Long sourceControlId = project.getSourceControl().getId();
        Integer gitlabProjectId = project.getGitlabProjectId();

        return releases.stream().map(r ->
                repository.findByUid(generateUniqueId(sourceControlId, gitlabProjectId, r.getName()))
                        .map(release -> syncRelease(r, release))
                        .orElseGet(() -> convertGitlabReleaseToMidasRelease(r, project))
        ).collect(Collectors.toSet());
    }

    protected Release convertGitlabReleaseToMidasRelease(GitLabRelease gitLabRelease, Project project) {
        String uId = generateUniqueId(project.getSourceControl().getId(), project.getGitlabProjectId(), gitLabRelease.getName());
        Release newRelease = new Release();

        BeanUtils.copyProperties(gitLabRelease, newRelease);
        newRelease.setUid(uId);
        newRelease.setProject(project);

        return repository.save(newRelease);
    }

    protected Release syncRelease(GitLabRelease gitLabRelease, Release release) {
        BeanUtils.copyProperties(gitLabRelease, release);
        return repository.save(release);
    }

    protected String generateUniqueId(Long sourceControlId, Integer gitlabProjectId, String releaseName) {
        return String.format("%d-%d-%s", sourceControlId, gitlabProjectId, releaseName);
    }

    protected GitLab4JClient getGitlabClient(Project project) {
        return new GitLab4JClient(project.getSourceControl());
    }

}
