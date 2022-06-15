package mil.af.abms.midas.api.release;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabRelease;

@ExtendWith(SpringExtension.class)
@Import(ReleaseService.class)
public class ReleaseServiceTests {

    @SpyBean
    private ReleaseService releaseService;
    @MockBean
    private ReleaseRepository repository;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private ProductService productService;
    @MockBean
    private GitLab4JClient gitLab4JClient;

    private static final LocalDateTime CREATED_AT = LocalDateTime.now();

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
    private final Release foundRelease = Builder.build(Release.class)
            .with(r -> r.setId(6L))
            .with(r -> r.setName("whoa this is release"))
            .with(r -> r.setCreationDate(CREATED_AT))
            .with(r -> r.setUid("3422"))
            .with(r -> r.setProject(foundProject))
            .get();
    private final GitLabRelease gitLabRelease = Builder.build(GitLabRelease.class)
            .with(gr -> gr.setName("whoa this is release"))
            .get();

    @Test
    void should_get_all_releases_by_project_id() {
        releaseService.getAllReleasesByProjectId(foundProject.getId());

        assertThat(releaseService.getAllReleasesByProjectId(foundProject.getId())).isNotNull();
    }

    @Test
    void should_get_all_releases_for_gitlab_project() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedRelease = new Release();
        BeanUtils.copyProperties(foundRelease, expectedRelease);
        expectedRelease.setName(gitLabRelease.getName());
        expectedRelease.setId(6L);

        doReturn(expectedRelease).when(repository).save(any(Release.class));
        when(releaseService.getGitlabClient(foundProject)).thenReturn(gitLab4JClient);
        doReturn(1).when(gitLab4JClient).getTotalReleasesPages(foundProject.getGitlabProjectId());
        doReturn(Set.of(expectedRelease)).when(releaseService).processReleases(List.of(), foundProject);
        doNothing().when(releaseService).removeAllUntrackedReleases(anyLong(), anySet());
        doReturn(foundRelease).when(releaseService).convertGitlabReleaseToMidasRelease(any(), any());

        assertThat(releaseService.gitlabReleaseSync(foundProject)).isEqualTo(Set.of(expectedRelease));
    }

    @Test
    void should_return_empty_set_when_project_is_archived() {
        foundProject.setIsArchived(true);

        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);

        assertThat(releaseService.gitlabReleaseSync(foundProject)).isEqualTo(Set.of());
    }

    @Test
    void should_return_empty_set_when_project_gitlab_id_is_null() {
        foundProject.setGitlabProjectId(null);

        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);

        assertThat(releaseService.gitlabReleaseSync(foundProject)).isEqualTo(Set.of());
    }

    @Test
    void should_return_empty_set_when_project_source_control_is_null() {
        foundProject.setSourceControl(null);

        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);

        assertThat(releaseService.gitlabReleaseSync(foundProject)).isEqualTo(Set.of());
    }

    @Test
    void should_return_empty_set_when_project_source_control_token_is_null() {
        sourceControl.setToken(null);
        foundProject.setSourceControl(sourceControl);

        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);

        assertThat(releaseService.gitlabReleaseSync(foundProject)).isEqualTo(Set.of());
    }

    @Test
    void should_return_empty_set_when_project_source_control_base_url_is_null() {
        sourceControl.setBaseUrl(null);
        foundProject.setSourceControl(sourceControl);

        when(projectService.findById(foundProject.getId())).thenReturn(foundProject);

        assertThat(releaseService.gitlabReleaseSync(foundProject)).isEqualTo(Set.of());
    }

    @Test
    void should_run_scheduled_release_sync() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedRelease = new Release();
        BeanUtils.copyProperties(gitLabRelease, expectedRelease);
        expectedRelease.setCreationDate(CREATED_AT);

        var archivedProject = new Project();
        BeanUtils.copyProperties(foundProject, archivedProject);
        archivedProject.setIsArchived(true);

        doReturn(List.of(foundProject, archivedProject)).when(projectService).getAll();
        doReturn(foundProject).when(projectService).findById(foundProject.getId());
        doReturn(gitLab4JClient).when(releaseService).getGitlabClient(any());
        doNothing().when(releaseService).removeAllUntrackedReleases(anyLong(), anySet());

        releaseService.runScheduledReleaseSync();

        verify(releaseService, times(1)).gitlabReleaseSync(foundProject);
    }

    @Test
    void should_remove_all_untracked_releases() {
        Release expectedRelease = new Release();
        BeanUtils.copyProperties(foundRelease, expectedRelease);
        expectedRelease.setName(gitLabRelease.getName());

        doNothing().when(repository).deleteAll(anyList());
        doReturn(new ArrayList<>(List.of(foundRelease))).when(releaseService).getAllReleasesByProjectId(foundProject.getId());

        releaseService.removeAllUntrackedReleases(foundProject.getId(), Set.of(foundRelease));

        verify(repository, times(1)).deleteAll(any());
    }

    @Test
    void Should_Get_All_Releases_By_ProductId() {
        when(productService.findById(anyLong())).thenReturn(foundProduct);
        doReturn(new ArrayList<>(List.of(foundRelease))).when(releaseService).getAllReleasesByProjectId(foundProject.getId());

        assertThat(releaseService.getAllReleasesByProductId(2L)).isEqualTo(List.of((foundRelease)));
    }

    @Test
    void Should_Sync_Gitlab_Release_For_Product() {
        when(productService.findById(anyLong())).thenReturn(foundProduct);
        doReturn(Set.of(foundRelease)).when(releaseService).gitlabReleaseSync(any());
        doReturn(true).when(releaseService).hasGitlabDetails(any());
        doNothing().when(projectService).updateReleaseSyncStatus(anyLong(), any());
        doReturn(gitLab4JClient).when(releaseService).getGitlabClient(any());
        doReturn(1).when(gitLab4JClient).getTotalReleasesPages(anyInt());
        doReturn(Set.of(foundRelease)).when(releaseService).processReleases(anyList(), any());
        doNothing().when(releaseService).removeAllUntrackedReleases(anyLong(), anySet());

        releaseService.syncGitlabReleaseForProject(1L);

        assertThat(releaseService.syncGitlabReleaseForProduct(2L)).isEqualTo(Set.of((foundRelease)));
    }

    @Test
    void Should_Process_Releases_found() {
        when(repository.findByUid(anyString())).thenReturn(Optional.of(foundRelease));
        when(repository.save(any())).thenReturn(foundRelease);

        assertThat(releaseService.processReleases(List.of(gitLabRelease), foundProject)).isEqualTo(Set.of(foundRelease));
    }

    @Test
    void Should_Process_Releases_new() {
        when(repository.findByUid(anyString())).thenReturn(Optional.empty());
        doReturn(foundRelease).when(releaseService).convertGitlabReleaseToMidasRelease(gitLabRelease, foundProject);

        assertThat(releaseService.processReleases(List.of(gitLabRelease), foundProject)).isEqualTo(Set.of(foundRelease));
    }

    @Test
    void Should_Convert_Gitlab_Release_To_Midas_Release() {
        doReturn("1-2-v1.2.0").when(releaseService).generateUniqueId(any(), any(), any());
        when(repository.save(any())).thenReturn(foundRelease);

        assertThat(releaseService.convertGitlabReleaseToMidasRelease(gitLabRelease, foundProject)).isEqualTo(foundRelease);
    }
}
