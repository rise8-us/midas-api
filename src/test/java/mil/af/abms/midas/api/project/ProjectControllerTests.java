package mil.af.abms.midas.api.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.dto.ArchiveProjectDTO;
import mil.af.abms.midas.api.project.dto.CreateProjectDTO;
import mil.af.abms.midas.api.project.dto.ProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectJourneyMapDTO;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.clients.GitLab4JClient;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({ProjectController.class})
public class ProjectControllerTests extends ControllerTestHarness {

    @MockBean
    private ProjectService projectService;
    @MockBean
    private TeamService teamService;
    @MockBean
    private TagService tagService;
    @MockBean
    private GitLab4JClient gitLab4JClient;

    private final static Long ID = 1L;
    private final static String NAME = "MIDAS";
    private final static String DESCRIPTION = "MIDAS Project";
    private final static Boolean IS_ARCHIVED = false;
    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final static Long TEAM_ID = 3L;
    private final static Long GITLAB_PROJECT_ID = 2L;
    private final static Long GITLAB_GROUP_ID = 3L;

    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(TEAM_ID))
            .with(t -> t.setName("MIDAS_TEAM"))
            .with(t -> t.setCreationDate(CREATION_DATE))
            .with(t -> t.setGitlabGroupId(GITLAB_GROUP_ID)).get();
    private final Project project = Builder.build(Project.class)
            .with(p -> p.setId(ID))
            .with(p -> p.setName(NAME))
            .with(p -> p.setGitlabProjectId(GITLAB_PROJECT_ID))
            .with(p -> p.setTeam(team))
            .with(p -> p.setDescription(DESCRIPTION))
            .with(p -> p.setCreationDate(CREATION_DATE))
            .with(p -> p.setProjectJourneyMap(0L))
            .with(p -> p.setIsArchived(IS_ARCHIVED)).get();
    private final Tag tag = Builder.build(Tag.class)
            .with(t -> t.setId(3L))
            .with(t -> t.setLabel("Tag"))
            .with(t -> t.setProjects(Set.of(project))).get();

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test //TODO: fix
    public void should_create_project() throws Exception {
        CreateProjectDTO createProjectDTO = new CreateProjectDTO(NAME, GITLAB_PROJECT_ID, 33L, Set.of(3L), DESCRIPTION, null);

        when(projectService.findByName(NAME)).thenThrow(EntityNotFoundException.class);
        when(projectService.create(any(CreateProjectDTO.class))).thenReturn(project);
        when(teamService.existsById(33L)).thenReturn(true);
        when(tagService.existsById(3L)).thenReturn(true);
        when(gitLab4JClient.projectExistsById(GITLAB_PROJECT_ID)).thenReturn(true);

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createProjectDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(NAME));
    }

    @Test
    public void should_update_project() throws Exception {
        UpdateProjectDTO updateProjectDTO = new UpdateProjectDTO(NAME, 5L, 0L, Set.of(tag.getId()), "", 1L);

        when(teamService.existsById(any())).thenReturn(true);
        when(tagService.existsById(any())).thenReturn(true);
        when(projectService.findByName(NAME)).thenReturn(project);
        when(projectService.updateById(anyLong(), any(UpdateProjectDTO.class))).thenReturn(project);
        when(gitLab4JClient.projectExistsById(5L)).thenReturn(true);

        mockMvc.perform(put("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateProjectDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(project.getName()));

    }

    @Test
    public void should_throw_team_exists_exception_on_update_project_team() throws Exception {
        UpdateProjectDTO updateProjectDTO = new UpdateProjectDTO(NAME, 5L, 1L, Set.of(tag.getId()), "", 1L);

        when(projectService.findByName(NAME)).thenReturn(project);
        when(tagService.existsById(any())).thenReturn(true);
        when(teamService.existsById(any())).thenReturn(false);
        when(gitLab4JClient.projectExistsById(any())).thenReturn(true);

        mockMvc.perform(put("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateProjectDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value("team does not exist"));
    }

    @Test
    public void should_throw_unique_name_exception_on_project() throws Exception {
        UpdateProjectDTO updateProjectDTO = new UpdateProjectDTO(NAME, 5L, 0L, Set.of(tag.getId()), "false", 1L);
        Project diffProjectSameName = new Project();
        BeanUtils.copyProperties(project, diffProjectSameName);
        diffProjectSameName.setId(42L);

        when(projectService.findByName(NAME)).thenReturn(diffProjectSameName);
        when(tagService.existsById(any())).thenReturn(true);
        when(teamService.existsById(any())).thenReturn(true);
        when(gitLab4JClient.projectExistsById(any())).thenReturn(true);

        mockMvc.perform(put("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateProjectDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value("project name already exists"));
    }

    @Test
    public void should_update_project_journey_map() throws Exception {
        UpdateProjectJourneyMapDTO updateJourneyMapDTO = Builder.build(UpdateProjectJourneyMapDTO.class)
                .with(p -> p.setProjectJourneyMap(1L)).get();
        ProjectDTO updateProjectDTO = project.toDto();
        updateProjectDTO.setProjectJourneyMap(0L);

        when(projectService.updateJourneyMapById(1L, updateJourneyMapDTO)).thenReturn(project);

        mockMvc.perform(put("/api/projects/1/journeymap")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateJourneyMapDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.projectJourneyMap").value(updateProjectDTO.getProjectJourneyMap()));
    }

    @Test
    public void should_update_project_is_archived_true() throws Exception {
        Project projectArchived = new Project();
        BeanUtils.copyProperties(project, projectArchived);
        projectArchived.setIsArchived(true);
        projectArchived.setTeam(null);
        ArchiveProjectDTO archiveProjectDTO = Builder.build(ArchiveProjectDTO.class)
                .with(d -> d.setIsArchived(true)).get();

        when(projectService.archive(any(), any())).thenReturn(projectArchived);

        mockMvc.perform(put("/api/projects/1/archive")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(archiveProjectDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(true))
                .andExpect(jsonPath("$.teamId").doesNotExist());
    }

    @Test
    public void should_update_project_is_archived_false() throws Exception {
        Project projectArchived = new Project();
        BeanUtils.copyProperties(project, projectArchived);
        projectArchived.setIsArchived(false);
        ArchiveProjectDTO archiveProjectDTO = Builder.build(ArchiveProjectDTO.class)
                .with(d -> d.setIsArchived(false)).get();

        when(projectService.archive(any(), any())).thenReturn(projectArchived);

        mockMvc.perform(put("/api/projects/1/archive")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(archiveProjectDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(false));
    }
}
