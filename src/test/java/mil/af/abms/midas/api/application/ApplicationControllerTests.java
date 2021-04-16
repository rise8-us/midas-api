package mil.af.abms.midas.api.application;

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
import mil.af.abms.midas.api.application.dto.CreateApplicationDTO;
import mil.af.abms.midas.api.application.dto.UpdateApplicationDTO;
import mil.af.abms.midas.api.application.dto.UpdateApplicationIsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({ApplicationController.class})
public class ApplicationControllerTests extends ControllerTestHarness {
    
    @MockBean
    ApplicationService applicationService;
    @MockBean
    TagService tagService;
    @MockBean
    ProjectService projectService;
    @MockBean
    TeamService teamService;

    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final UpdateApplicationDTO updateApplicationDTO = new UpdateApplicationDTO("Midas", 3L, "Full Stack",
            Set.of(3L), Set.of(3L), 1L);
    private final CreateApplicationDTO createApplicationDTO = new CreateApplicationDTO("Midas", 1L, "backend",
            Set.of(3L), Set.of(3L), 1L);
    private final Application application = Builder.build(Application.class)
            .with(p -> p.setId(5L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setProductManager(new User()))
            .with(p -> p.setDescription("stack full"))
            .with(p -> p.setCreationDate(CREATION_DATE))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setProjects(Set.of(new Project()))).get();

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_application() throws Exception {
        when(applicationService.findByName(createApplicationDTO.getName())).thenThrow(EntityNotFoundException.class);
        when(applicationService.create(any(CreateApplicationDTO.class))).thenReturn(application);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(projectService.existsById(anyLong())).thenReturn(true);
        when(tagService.existsById(any())).thenReturn(true);

        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createApplicationDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("Midas"));
    }

    @Test
    public void should_update_application_by_id() throws Exception {
        when(applicationService.findByName(updateApplicationDTO.getName())).thenReturn(application);
        when(applicationService.updateById(anyLong(), any(UpdateApplicationDTO.class))).thenReturn(application);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(projectService.existsById(anyLong())).thenReturn(true);
        when(tagService.existsById(any())).thenReturn(true);

        mockMvc.perform(put("/api/applications/5")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateApplicationDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("Midas"));
    }

    @Test
    public void should_throw_unique_name_validation_on_create() throws Exception {
        String errorMessage = "application name already exists";

        when(applicationService.findByName(createApplicationDTO.getName())).thenReturn(application);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(tagService.existsById(any())).thenReturn(true);
        when(projectService.existsById(any())).thenReturn(true);

        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createApplicationDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(errorMessage));
    }

    @Test
    public void should_throw_unique_name_validation_error_update_application_by_id() throws Exception {
        String errorMessage = "application name already exists";
        Application existingApplication = new Application();
        BeanUtils.copyProperties(application, existingApplication);
        existingApplication.setId(10L);

        when(applicationService.findByName(updateApplicationDTO.getName())).thenReturn(existingApplication);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(tagService.existsById(any())).thenReturn(true);
        when(projectService.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(put("/api/applications/5")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateApplicationDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(errorMessage));
    }

    @Test
    public void should_toggle_application_is_archived() throws Exception {
        UpdateApplicationIsArchivedDTO archivedDTO = Builder.build(UpdateApplicationIsArchivedDTO.class)
                .with(d -> d.setIsArchived(true)).get();
        application.setIsArchived(true);

        when(applicationService.updateIsArchivedById(5L, archivedDTO)).thenReturn(application);

        mockMvc.perform(put("/api/applications/5/archive")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(archivedDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(application.getIsArchived()));
    }

}
