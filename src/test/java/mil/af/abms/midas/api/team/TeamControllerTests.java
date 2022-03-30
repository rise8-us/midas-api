package mil.af.abms.midas.api.team;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({TeamController.class})
class TeamControllerTests extends ControllerTestHarness {

    @MockBean
    private TeamService teamService;

    private final CreateTeamDTO createTeamDTO = Builder.build(CreateTeamDTO.class)
            .with(d -> d.setName("teamName"))
            .with(d -> d.setDescription("description"))
            .with(d -> d.setProductManagerId(3L))
            .with(d -> d.setDesignerId(3L))
            .with(d -> d.setTechLeadId(3L))
            .with(d -> d.setUserIds(Set.of(3L)))
            .with(d -> d.setPersonnelIds(Set.of()))
            .get();
    private final UpdateTeamDTO updateTeamDTO = Builder.build(UpdateTeamDTO.class)
            .with(d -> d.setName("teamName updated"))
            .with(d -> d.setDescription("description updated"))
            .with(d -> d.setProductManagerId(3L))
            .with(d -> d.setDesignerId(3L))
            .with(d -> d.setTechLeadId(3L))
            .with(d -> d.setUserIds(Set.of(3L)))
            .with(d -> d.setPersonnelIds(Set.of()))
            .get();
    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setName("teamName"))
            .with(t -> t.setIsArchived(false))
            .with(t -> t.setDescription("description"))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_team() throws Exception {
        when(teamService.findByName(anyString())).thenThrow(EntityNotFoundException.class);
        when(teamService.create(any(CreateTeamDTO.class))).thenReturn(team);
        when(userService.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createTeamDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(createTeamDTO.getName()));
    }

    @Test
    void should_update_team_by_id() throws Exception {
        when(teamService.findByName(updateTeamDTO.getName())).thenReturn(team);
        when(teamService.updateById(anyLong(), any(UpdateTeamDTO.class))).thenReturn(team);
        when(userService.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(put("/api/teams/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateTeamDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(team.getName()));
    }

    @Test
    void should_throw_unique_name_validation_error_update_team_by_id() throws Exception {
        String expectedMessage = "team name already exists";
        Team existingTeam = new Team();
        BeanUtils.copyProperties(team, existingTeam);
        existingTeam.setId(3L);

        when(teamService.findByName(updateTeamDTO.getName())).thenReturn(existingTeam);
        when(userService.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(put("/api/teams/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateTeamDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(expectedMessage));
    }

    @Test
    void should_throw_unique_name_validation_on_create() throws Exception {
        String expectedMessage = "team name already exists";

        when(teamService.findByName(updateTeamDTO.getName())).thenReturn(team);
        when(userService.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateTeamDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(expectedMessage));
    }

    @Test
    void should_toggle_team_is_archived() throws Exception {
        IsArchivedDTO updateTeamIsArchivedDTO = Builder.build(IsArchivedDTO.class)
                .with(d -> d.setIsArchived(true)).get();
        team.setIsArchived(true);

        when(teamService.updateIsArchivedById(1L, updateTeamIsArchivedDTO)).thenReturn(team);

        mockMvc.perform(put("/api/teams/1/archive")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateTeamIsArchivedDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(team.getIsArchived()));
    }
}
