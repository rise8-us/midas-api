package mil.af.abms.midas.api.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.application.ApplicationService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.dto.ArchiveProjectDTO;
import mil.af.abms.midas.api.project.dto.CreateProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectJourneyMapDTO;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(ProjectService.class)
public class ProjectServiceTests {

    @Autowired
    ProjectService projectService;
    @MockBean
    ApplicationService applicationService;
    @MockBean
    TeamService teamService;
    @MockBean
    TagService tagService;
    @MockBean
    ProjectRepository projectRepository;

    @Captor
    ArgumentCaptor<Project> projectCaptor;

    Tag tagInProject = Builder.build(Tag.class)
            .with(t -> t.setId(22L))
            .with(t -> t.setLabel("TagInProject")).get();
    Tag tagTwoInProject = Builder.build(Tag.class)
            .with(t -> t.setId(21L))
            .with(t -> t.setLabel("TagTwoInProject")).get();
    Team team = Builder.build(Team.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setName("Team")).get();
    Project project = Builder.build(Project.class)
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setTeam(team))
            .with(p -> p.setTags(Set.of(tagInProject, tagTwoInProject)))
            .with(p -> p.setId(1L)).get();
    Tag tag = Builder.build(Tag.class)
            .with(t -> t.setId(3L))
            .with(t -> t.setLabel("Tag"))
            .with(t -> t.setProjects(Set.of(project))).get();

    @Test  //TODO: fix
    public void should_create_project() {
        CreateProjectDTO createProjectDTO = new CreateProjectDTO("MIDAS", 2L, 33L, Set.of(3L),
                "Project Description", null);

        when(projectRepository.save(project)).thenReturn(new Project());

        projectService.create(createProjectDTO);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

        assertThat(projectSaved.getName()).isEqualTo(createProjectDTO.getName());
        assertThat(projectSaved.getDescription()).isEqualTo(createProjectDTO.getDescription());
        assertThat(projectSaved.getGitlabProjectId()).isEqualTo(createProjectDTO.getGitlabProjectId());
    }

    @Test
    public void should_find_by_name() throws EntityNotFoundException {
        when(projectRepository.findByName("MIDAS")).thenReturn(Optional.of(project));

        assertThat(projectService.findByName("MIDAS")).isEqualTo(project);
    }

    @Test
    public void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                projectService.findByName("MIDAS"));
    }

    @Test
    public void should_update_project_by_id() {
        UpdateProjectDTO updateProjectDTO = new UpdateProjectDTO(
                "MIDAS_TWO", 5L, 22L, Set.of(tag.getId()), "New Description", true,
                1L);
        Team newTeam = new Team();
        BeanUtils.copyProperties(team, newTeam);
        newTeam.setId(22L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(teamService.findByIdOrNull(updateProjectDTO.getTeamId())).thenReturn(newTeam);

        projectService.updateById(1L, updateProjectDTO);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

        assertThat(projectSaved.getName()).isEqualTo(updateProjectDTO.getName());
        assertThat(projectSaved.getDescription()).isEqualTo(updateProjectDTO.getDescription());
        assertThat(projectSaved.getIsArchived()).isEqualTo(updateProjectDTO.getIsArchived());
        assertThat(projectSaved.getGitlabProjectId()).isEqualTo(updateProjectDTO.getGitlabProjectId());
        assertThat(projectSaved.getTeam().getId()).isEqualTo(updateProjectDTO.getTeamId());
    }

    @Test
    public void should_set_team_to_null() {
        UpdateProjectDTO updateDTO = Builder.build(UpdateProjectDTO.class)
                .with(d -> d.setName("projects"))
                .with(d -> d.setGitlabProjectId(1L))
                .with(d -> d.setTagIds(Set.of())).get();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(tagService.getObject(1L)).thenReturn(tag);

        projectService.updateById(1L, updateDTO);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

        assertThat(projectSaved.getTeam()).isEqualTo(null);
    }

    @Test
    public void should_set_tag_to_empty_set() {
        UpdateProjectDTO updateDTO = Builder.build(UpdateProjectDTO.class)
                .with(d -> d.setTagIds(Set.of())).get();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(tagService.getObject(1L)).thenReturn(tag);

        projectService.updateById(1L, updateDTO);

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();

       assertThat(projectSaved.getTags()).isEqualTo(Set.of());
    }

    @Test
    public void should_remove_tag_from_projects() {
        projectService.removeTagFromProjects(tagInProject.getId(), Set.of(project));

        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project projectSaved = projectCaptor.getValue();
        assertThat(projectSaved.getTags()).isEqualTo(Set.of(tagTwoInProject));
    }

    @Test
    public void should_remove_tag_from_project() {
       projectService.removeTagFromProject(tagInProject.getId(), project);

       Set<Tag> tagsToKeep = Set.of(tagTwoInProject);

       verify(projectRepository, times(1)).save(projectCaptor.capture());
       Project projectSaved = projectCaptor.getValue();
        assertThat(projectSaved.getTags()).isEqualTo(tagsToKeep);
    }

    @Test
    public void should_update_project_journey_map_by_id() {
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
    public void should_archive_project() {
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
    public void should_un_archive_project() {
        ArchiveProjectDTO archiveProjectDTO = Builder.build(ArchiveProjectDTO.class)
                .with(d -> d.setIsArchived(false)).get();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(this.project));

        Project project = projectService.archive(1L, archiveProjectDTO);

        verify(projectRepository).save(projectCaptor.capture());
        Project projectCaptured = projectCaptor.getValue();
        assertFalse(projectCaptured.getIsArchived());
        assertThat(projectCaptured.getTeam()).isEqualTo(null);
    }
}
