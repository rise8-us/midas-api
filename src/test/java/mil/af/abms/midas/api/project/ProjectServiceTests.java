package mil.af.abms.midas.api.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.coverage.Coverage;
import mil.af.abms.midas.api.coverage.CoverageService;
import mil.af.abms.midas.api.gitlabconfig.GitlabConfig;
import mil.af.abms.midas.api.gitlabconfig.GitlabConfigService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.dto.ArchiveProjectDTO;
import mil.af.abms.midas.api.project.dto.CreateProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectJourneyMapDTO;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
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
    GitlabConfigService gitlabConfigService;
    @MockBean
    CustomProperty property;
    @MockBean
    ProductService productService;
    @MockBean
    TeamService teamService;
    @MockBean
    TagService tagService;
    @MockBean
    ProjectRepository projectRepository;

    @Captor
    ArgumentCaptor<Project> projectCaptor;

    private final Tag tagInProject = Builder.build(Tag.class)
            .with(t -> t.setId(22L))
            .with(t -> t.setLabel("TagInProject")).get();
    private final Tag tagTwoInProject = Builder.build(Tag.class)
            .with(t -> t.setId(21L))
            .with(t -> t.setLabel("TagTwoInProject")).get();
    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setName("Team")).get();
    private final Project project = Builder.build(Project.class)
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setTeam(team))
            .with(p -> p.setTags(Set.of(tagInProject, tagTwoInProject)))
            .with(p -> p.setId(1L)).get();
    private final Tag tag = Builder.build(Tag.class)
            .with(t -> t.setId(3L))
            .with(t -> t.setLabel("Tag"))
            .with(t -> t.setProjects(Set.of(project))).get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setProjects(Set.of(project))).get();
    private final GitlabConfig gitlabConfig = Builder.build(GitlabConfig.class)
            .with(g -> g.setId(42L))
            .with(g -> g.setName("Mock IL2"))
            .get();

    @Test
    void should_create_project() {
        CreateProjectDTO createProjectDTO = new CreateProjectDTO("MIDAS", 2, 33L, Set.of(3L),
                "Project Description", null, 42L);

        when(gitlabConfigService.findByIdOrNull(42L)).thenReturn(gitlabConfig);
        when(projectRepository.save(project)).thenReturn(new Project());

        projectService.create(createProjectDTO);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

        assertThat(projectSaved.getName()).isEqualTo(createProjectDTO.getName());
        assertThat(projectSaved.getDescription()).isEqualTo(createProjectDTO.getDescription());
        assertThat(projectSaved.getGitlabProjectId()).isEqualTo(createProjectDTO.getGitlabProjectId());
        assertThat(projectSaved.getGitlabConfig()).isEqualTo(gitlabConfig);
    }

    @Test
    void should_find_by_name() throws EntityNotFoundException {
        when(projectRepository.findByName("MIDAS")).thenReturn(Optional.of(project));

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
        GitlabConfig config2 = new GitlabConfig();
        BeanUtils.copyProperties(gitlabConfig, config2);
        config2.setId(43L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(teamService.findByIdOrNull(updateProjectDTO.getTeamId())).thenReturn(newTeam);
        when(gitlabConfigService.findByIdOrNull(43L)).thenReturn(config2);

        projectService.updateById(1L, updateProjectDTO);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

        assertThat(projectSaved.getName()).isEqualTo(updateProjectDTO.getName());
        assertThat(projectSaved.getDescription()).isEqualTo(updateProjectDTO.getDescription());
        assertThat(projectSaved.getGitlabProjectId()).isEqualTo(updateProjectDTO.getGitlabProjectId());
        assertThat(projectSaved.getTeam()).isEqualTo(newTeam);
        assertThat(projectSaved.getGitlabConfig()).isEqualTo(config2);
    }

    @Test
    void should_set_tag_to_empty_set() {
        UpdateProjectDTO updateDTO = Builder.build(UpdateProjectDTO.class)
                .with(d -> d.setTagIds(Set.of())).get();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(tagService.findById(1L)).thenReturn(tag);

        projectService.updateById(1L, updateDTO);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

       assertThat(projectSaved.getTags()).isEqualTo(Set.of());
    }

    @Test
    void should_remove_tag_from_projects() {
        projectService.removeTagFromProjects(tagInProject.getId(), Set.of(project));

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();
        assertThat(projectSaved.getTags()).isEqualTo(Set.of(tagTwoInProject));
    }

    @Test
    void should_remove_tag_from_project() {
       projectService.removeTagFromProject(tagInProject.getId(), project);

       Set<Tag> tagsToKeep = Set.of(tagTwoInProject);

       verify(projectRepository, times(1)).save(projectCaptor.capture());
       Project projectSaved = projectCaptor.getValue();
        assertThat(projectSaved.getTags()).isEqualTo(tagsToKeep);
    }

    @Test
    void should_update_project_journey_map_by_id() {
        UpdateProjectJourneyMapDTO updateJourneyMapDTO = Builder.build(UpdateProjectJourneyMapDTO.class)
                .with(p -> p.setProjectJourneyMap(0L)).get();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        projectService.updateJourneyMapById(1L, updateJourneyMapDTO);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

        assertThat(projectSaved.getProjectJourneyMap()).isEqualTo(updateJourneyMapDTO.getProjectJourneyMap());
    }

    @Test
    void should_archive_project() {
        ArchiveProjectDTO archiveProjectDTO = Builder.build(ArchiveProjectDTO.class)
                .with(d -> d.setIsArchived(true)).get();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(this.project));

        Project project = projectService.archive(1L, archiveProjectDTO);

        verify(projectRepository).save(projectCaptor.capture());
        Project projectCaptured = projectCaptor.getValue();
        assertTrue(projectCaptured.getIsArchived());
        assertThat(projectCaptured.getTeam()).isEqualTo(null);
    }

    @Test
    void should_un_archive_project() {
        ArchiveProjectDTO archiveProjectDTO = Builder.build(ArchiveProjectDTO.class)
                .with(d -> d.setIsArchived(false)).get();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(this.project));

        Project project = projectService.archive(1L, archiveProjectDTO);

        verify(projectRepository).save(projectCaptor.capture());
        Project projectCaptured = projectCaptor.getValue();
        assertFalse(projectCaptured.getIsArchived());
        assertThat(projectCaptured.getTeam()).isEqualTo(null);
    }

    @Test
    void should_add_product_to_set_of_projects() {
        Project projectInSet = new Project();
        BeanUtils.copyProperties(project, projectInSet);
        Set<Project> projects = Set.of(projectInSet);

        projectService.addProductToProjects(product, projects);

        verify(projectRepository).save(projectCaptor.capture());
        Project projectCaptured = projectCaptor.getValue();
        assertThat(projectCaptured.getProduct()).isEqualTo(product);
    }

    @Test
    void should_add_product_to_project() {
        Project projectWithApp = new Project();
        BeanUtils.copyProperties(project, projectWithApp);

        projectService.addProductToProject(product, projectWithApp);

        verify(projectRepository).save(projectCaptor.capture());
        Project projectCaptured = projectCaptor.getValue();
        assertThat(projectCaptured.getProduct()).isEqualTo(product);
    }

    @Test
    void should_create_project_with_null_product_id_and_null_team_id() {
        CreateProjectDTO createDTO = new CreateProjectDTO("No Product", 20, null, Set.of(3L),
                "Project Description", null, null);

        when(projectRepository.save(project)).thenReturn(new Project());

        projectService.create(createDTO);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

        assertThat(projectSaved.getProduct()).isNull();
        assertThat(projectSaved.getTeam()).isNull();
        assertThat(projectSaved.getGitlabConfig()).isNull();
    }

    @Test
    void should_update_project_with_null_product_id_and_null_team_id() {
        UpdateProjectDTO updateProjectDTO = new UpdateProjectDTO(
                "MIDAS_TWO", 5, null, Set.of(tag.getId()), "New Description",
                null, null);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        projectService.updateById(1L, updateProjectDTO);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

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

        verify(projectRepository, times(2)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

        assertThat(projectSaved.getId()).isEqualTo(project2.getId());
    }

    @Test
    @SuppressWarnings(value = "unchecked")
    void should_run_scheduled_coverage_update_projects() {
        GitlabConfig config = new GitlabConfig();
        config.setId(1L);
        Project p2 = new Project();
        project.setGitlabConfig(config);
        BeanUtils.copyProperties(project, p2);

        when(projectRepository.findAll(any(Specification.class))).thenReturn(List.of(project, p2));
        when(coverageService.updateCoverageForProject(any())).thenReturn(new Coverage());

        projectService.scheduledCoverageUpdates();

        verify(projectRepository, times(1)).findAll(any(Specification.class));
        verify(coverageService, times(2)).updateCoverageForProject(any());
    }

}
