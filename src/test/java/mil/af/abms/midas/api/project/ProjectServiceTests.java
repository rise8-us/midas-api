package mil.af.abms.midas.api.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.coverage.Coverage;
import mil.af.abms.midas.api.coverage.CoverageService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.dto.CreateProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectJourneyMapDTO;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabProject;
import mil.af.abms.midas.config.CustomProperty;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(ProjectService.class)
class ProjectServiceTests {

    @SpyBean
    ProjectService projectService;
    @MockBean
    CoverageService coverageService;
    @MockBean
    SourceControlService sourceControlService;
    @MockBean
    CustomProperty property;
    @MockBean
    ProductService productService;
    @MockBean
    TeamService teamService;
    @MockBean
    TagService tagService;
    @MockBean
    ProjectRepository repository;
    @MockBean
    SimpMessageSendingOperations websocket;
    @MockBean
    UserService userService;
    @MockBean
    GitLab4JClient client;

    @Captor
    ArgumentCaptor<Project> captor;

    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(g -> g.setId(42L))
            .with(g -> g.setName("Mock IL2"))
            .get();
    private final Tag tagInProject = Builder.build(Tag.class)
            .with(t -> t.setId(22L))
            .with(t -> t.setLabel("TagInProject"))
            .get();
    private final Tag tagTwoInProject = Builder.build(Tag.class)
            .with(t -> t.setId(21L))
            .with(t -> t.setLabel("TagTwoInProject"))
            .get();
    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setName("Team"))
            .get();
    private final Project project = Builder.build(Project.class)
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setTeam(team))
            .with(p -> p.setTags(Set.of(tagInProject, tagTwoInProject)))
            .with(p -> p.setId(1L))
            .with(p -> p.setGitlabProjectId(2))
            .with(p -> p.setSourceControl(sourceControl))
            .get();
    private final Tag tag = Builder.build(Tag.class)
            .with(t -> t.setId(3L))
            .with(t -> t.setLabel("Tag"))
            .with(t -> t.setProjects(Set.of(project)))
            .get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setProjects(Set.of(project)))
            .get();
    private final Coverage coverage = Builder.build(Coverage.class)
            .with(c -> c.setId(4L))
            .get();
    private final GitLabProject gitLabProject = Builder.build(GitLabProject.class)
            .with(p -> p.setName("title"))
            .with(p -> p.setGitlabProjectId(2))
            .get();

    @Test
    void should_create_project() {
        CreateProjectDTO createProjectDTO = new CreateProjectDTO("MIDAS", 2, 33L, Set.of(3L),
                "Project Description", null, 42L);

        when(sourceControlService.findByIdOrNull(42L)).thenReturn(sourceControl);
        when(repository.save(project)).thenReturn(new Project());

        projectService.create(createProjectDTO);

        verify(repository, times(1)).save(captor.capture());
        Project projectSaved = captor.getValue();

        assertThat(projectSaved.getName()).isEqualTo(createProjectDTO.getName());
        assertThat(projectSaved.getDescription()).isEqualTo(createProjectDTO.getDescription());
        assertThat(projectSaved.getGitlabProjectId()).isEqualTo(createProjectDTO.getGitlabProjectId());
        assertThat(projectSaved.getSourceControl()).isEqualTo(sourceControl);
    }

    @Test
    void should_create_project_from_gitlab() {
        projectService.createFromGitlab(gitLabProject);
        verify(repository, times(1)).save(captor.capture());
        var projectSaved = captor.getValue();

        assertThat(projectSaved.getName()).isEqualTo("title");
        assertThat(projectSaved.getGitlabProjectId()).isEqualTo(2);
    }

    @Test
    void should_sync_project_from_gitlab() {
        doReturn(project).when(projectService).findById(project.getId());
        doReturn(client).when(projectService).getGitlabClient(sourceControl);
        doReturn(gitLabProject).when(client).getGitLabProject(project.getGitlabProjectId());

        projectService.syncProjectWithGitlab(project.getId());
        verify(repository, times(1)).save(captor.capture());
        var projectSaved = captor.getValue();

        assertThat(projectSaved.getName()).isEqualTo(gitLabProject.getName());
        assertThat(projectSaved.getGitlabProjectId()).isEqualTo(gitLabProject.getGitlabProjectId());
    }

    @Test
    void should_find_by_name() throws EntityNotFoundException {
        when(repository.findByName("MIDAS")).thenReturn(Optional.of(project));

        assertThat(projectService.findByName("MIDAS")).isEqualTo(project);
    }

    @Test
    void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                projectService.findByName("MIDAS"));
    }

    @Test
    void should_update_project_by_id() {
        UpdateProjectDTO updateProjectDTO = new UpdateProjectDTO(
                "MIDAS_TWO", 5, 22L, Set.of(tag.getId()), "New Description",
                1L, 43L);
        Team newTeam = new Team();
        BeanUtils.copyProperties(team, newTeam);
        newTeam.setId(22L);
        SourceControl config2 = new SourceControl();
        BeanUtils.copyProperties(sourceControl, config2);
        config2.setId(43L);

        when(repository.findById(1L)).thenReturn(Optional.of(project));
        when(repository.save(project)).thenReturn(project);
        when(teamService.findByIdOrNull(updateProjectDTO.getTeamId())).thenReturn(newTeam);
        when(sourceControlService.findByIdOrNull(43L)).thenReturn(config2);
        doNothing().when(projectService).addCoverageOnCreateOrUpdate(any());

        projectService.updateById(1L, updateProjectDTO);

        verify(repository, times(1)).save(captor.capture());
        Project projectSaved = captor.getValue();

        assertThat(projectSaved.getName()).isEqualTo(updateProjectDTO.getName());
        assertThat(projectSaved.getDescription()).isEqualTo(updateProjectDTO.getDescription());
        assertThat(projectSaved.getGitlabProjectId()).isEqualTo(updateProjectDTO.getGitlabProjectId());
        assertThat(projectSaved.getTeam()).isEqualTo(newTeam);
        assertThat(projectSaved.getSourceControl()).isEqualTo(config2);
    }

    @Test
    void should_set_tag_to_empty_set() {
        UpdateProjectDTO updateDTO = Builder.build(UpdateProjectDTO.class)
                .with(d -> d.setTagIds(Set.of())).get();

        when(repository.findById(1L)).thenReturn(Optional.of(project));
        when(tagService.findById(1L)).thenReturn(tag);
        doNothing().when(projectService).addCoverageOnCreateOrUpdate(any());

        projectService.updateById(1L, updateDTO);

        verify(repository, times(1)).save(captor.capture());
        Project projectSaved = captor.getValue();

       assertThat(projectSaved.getTags()).isEqualTo(Set.of());
    }

    @Test
    void should_remove_tag_from_projects() {
        projectService.removeTagFromProjects(tagInProject.getId(), Set.of(project));

        verify(repository, times(1)).save(captor.capture());
        Project projectSaved = captor.getValue();
        assertThat(projectSaved.getTags()).isEqualTo(Set.of(tagTwoInProject));
    }

    @Test
    void should_remove_tag_from_project() {
       projectService.removeTagFromProject(tagInProject.getId(), project);

       Set<Tag> tagsToKeep = Set.of(tagTwoInProject);

       verify(repository, times(1)).save(captor.capture());
       Project projectSaved = captor.getValue();
        assertThat(projectSaved.getTags()).isEqualTo(tagsToKeep);
    }

    @Test
    void should_update_project_journey_map_by_id() {
        UpdateProjectJourneyMapDTO updateJourneyMapDTO = Builder.build(UpdateProjectJourneyMapDTO.class)
                .with(p -> p.setProjectJourneyMap(0L)).get();

        when(repository.findById(1L)).thenReturn(Optional.of(project));
        when(repository.save(project)).thenReturn(project);

        projectService.updateJourneyMapById(1L, updateJourneyMapDTO);

        verify(repository, times(1)).save(captor.capture());
        Project projectSaved = captor.getValue();

        assertThat(projectSaved.getProjectJourneyMap()).isEqualTo(updateJourneyMapDTO.getProjectJourneyMap());
    }

    @Test
    void should_archive_project() {
        IsArchivedDTO archiveProjectDTO = Builder.build(IsArchivedDTO.class)
                .with(d -> d.setIsArchived(true)).get();

        when(repository.findById(1L)).thenReturn(Optional.of(this.project));

        projectService.archive(1L, archiveProjectDTO);

        verify(repository).save(captor.capture());
        Project projectCaptured = captor.getValue();
        assertTrue(projectCaptured.getIsArchived());
        assertThat(projectCaptured.getTeam()).isEqualTo(null);
    }

    @Test
    void should_un_archive_project() {
        IsArchivedDTO archiveProjectDTO = Builder.build(IsArchivedDTO.class)
                .with(d -> d.setIsArchived(false)).get();

        when(repository.findById(1L)).thenReturn(Optional.of(this.project));

        projectService.archive(1L, archiveProjectDTO);

        verify(repository).save(captor.capture());
        Project projectCaptured = captor.getValue();
        assertFalse(projectCaptured.getIsArchived());
        assertThat(projectCaptured.getTeam()).isEqualTo(null);
    }

    @Test
    void should_add_product_to_set_of_projects() {
        Project projectInSet = new Project();
        BeanUtils.copyProperties(project, projectInSet);
        Set<Project> projects = Set.of(projectInSet);

        projectService.addProductToProjects(product, projects);

        verify(repository).save(captor.capture());
        Project projectCaptured = captor.getValue();
        assertThat(projectCaptured.getProduct()).isEqualTo(product);
    }

    @Test
    void should_add_product_to_project() {
        Project projectWithApp = new Project();
        BeanUtils.copyProperties(project, projectWithApp);

        projectService.addProductToProject(product, projectWithApp);

        verify(repository).save(captor.capture());
        Project projectCaptured = captor.getValue();
        assertThat(projectCaptured.getProduct()).isEqualTo(product);
    }

    @Test
    void should_create_project_with_null_product_id_and_null_team_id() {
        CreateProjectDTO createDTO = new CreateProjectDTO("No Product", 20, null, Set.of(3L),
                "Project Description", null, null);

        when(repository.save(project)).thenReturn(new Project());
        doNothing().when(projectService).addCoverageOnCreateOrUpdate(any());

        projectService.create(createDTO);

        verify(repository, times(1)).save(captor.capture());
        Project projectSaved = captor.getValue();

        assertThat(projectSaved.getProduct()).isNull();
        assertThat(projectSaved.getTeam()).isNull();
        assertThat(projectSaved.getSourceControl()).isNull();
    }

    @Test
    void should_update_project_with_null_product_id_and_null_team_id() {
        UpdateProjectDTO updateProjectDTO = new UpdateProjectDTO(
                "MIDAS_TWO", 5, null, Set.of(tag.getId()), "New Description",
                null, null);

        when(repository.findById(1L)).thenReturn(Optional.of(project));
        when(repository.save(project)).thenReturn(project);

        projectService.updateById(1L, updateProjectDTO);

        verify(repository, times(1)).save(captor.capture());
        Project projectSaved = captor.getValue();

        assertThat(projectSaved.getProduct()).isEqualTo(null);
        assertThat(projectSaved.getTeam()).isEqualTo(null);
    }

    @Test
    void should_update_product_with_projects() {
        Project project2 = Builder.build(Project.class)
                .with(p -> p.setName("API"))
                .with(p -> p.setId(3L)).get();
        Set<Project> updatedProjects = new HashSet<>();
        updatedProjects.add(project2);
        Product initialProduct = Builder.build(Product.class)
                .with(p -> p.setId(2L))
                .with(p -> p.setProjects(Set.of(project, project2))).get();

        projectService.updateProjectsWithProduct(initialProduct.getProjects(), updatedProjects, initialProduct);

        verify(repository, times(2)).save(captor.capture());
        Project projectSaved = captor.getValue();

        assertThat(projectSaved.getId()).isEqualTo(project2.getId());
    }

    @Test
    @SuppressWarnings(value = "unchecked")
    void should_run_scheduled_coverage_update_projects() {
        SourceControl config = new SourceControl();
        config.setId(1L);
        Project p2 = new Project();
        project.setSourceControl(config);
        BeanUtils.copyProperties(project, p2);

        when(repository.findAll(any(Specification.class))).thenReturn(List.of(project, p2));
        when(coverageService.updateCoverageForProject(any())).thenReturn(new Coverage());

        projectService.scheduledCoverageUpdates();

        verify(repository, times(1)).findAll(any(Specification.class));
        verify(coverageService, times(2)).updateCoverageForProject(any());
    }

    @Test
    void should_add_coverage_on_create_or_update() {
        SourceControl sourceControl = new SourceControl();
        sourceControl.setId(1L);
        Project p2 = new Project();
        BeanUtils.copyProperties(project, p2);
        p2.setSourceControl(sourceControl);
        p2.setGitlabProjectId(2);

        when(coverageService.updateCoverageForProject(p2)).thenReturn(coverage);
        projectService.addCoverageOnCreateOrUpdate(p2);

        verify(coverageService, times(1)).updateCoverageForProject(p2);
        verify(websocket, times(1)).convertAndSend("/topic/update_project", p2.toDto());
    }

    @Test
    void should_skip_add_coverage_on_create_or_update() {
        SourceControl sourceControl = new SourceControl();
        sourceControl.setId(1L);
        Project p2 = new Project();
        BeanUtils.copyProperties(project, p2);
        p2.setSourceControl(sourceControl);
        p2.setGitlabProjectId(2);

        p2.setSourceControl(null);
        projectService.addCoverageOnCreateOrUpdate(p2);

        verify(coverageService, times(0)).updateCoverageForProject(p2);
        verify(websocket, times(0)).convertAndSend("/topic/update_project", p2.toDto());

        p2.setGitlabProjectId(null);
        projectService.addCoverageOnCreateOrUpdate(p2);

        verify(coverageService, times(0)).updateCoverageForProject(p2);
        verify(websocket, times(0)).convertAndSend("/topic/update_project", p2.toDto());

        p2.setSourceControl(sourceControl);
        projectService.addCoverageOnCreateOrUpdate(p2);

        verify(coverageService, times(0)).updateCoverageForProject(p2);
        verify(websocket, times(0)).convertAndSend("/topic/update_project", p2.toDto());

    }
}
