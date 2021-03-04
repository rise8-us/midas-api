package mil.af.abms.midas.api.team;

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

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({TeamController.class})
public class TeamControllerTests extends ControllerTestHarness {

    @MockBean
    private TeamService teamService;

    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();

    private UpdateTeamDTO updateTeamDTO = new UpdateTeamDTO("MIDAS", false, 5L);
    private CreateTeamDTO createTeamDTO = new CreateTeamDTO("MIDAS", 1L);
    private TeamEntity team = Builder.build(TeamEntity.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setIsArchived(false))
            .with(t -> t.setGitlabGroupId(5L)).get();

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_Create_Team() throws Exception {
        when(teamService.findByName("MIDAS")).thenThrow(EntityNotFoundException.class);
        when(teamService.create(any(CreateTeamDTO.class))).thenReturn(team);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createTeamDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("MIDAS"));
    }

    @Test
    public void should_Update_Team_By_Id() throws Exception {
        when(teamService.findByName(updateTeamDTO.getName())).thenReturn(team);
        when(teamService.updateById(anyLong(), any(UpdateTeamDTO.class))).thenReturn(team);

        mockMvc.perform(put("/api/teams/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateTeamDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(team.getName()));
    }

    @Test
    public void should_Throw_Unique_Name_Validation_Error_Update_Team_By_Id() throws Exception {
        String expectedMessage = "team name already exists";
        TeamEntity existingTeam = new TeamEntity();
        BeanUtils.copyProperties(team, existingTeam);
        existingTeam.setId(3L);

        when(teamService.findByName(updateTeamDTO.getName())).thenReturn(existingTeam);

        mockMvc.perform(put("/api/teams/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateTeamDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(expectedMessage));
    }

    @Test
    public void should_Throw_Unique_Name_Validation_On_Create() throws Exception {
        String expectedMessage = "team name already exists";

        when(teamService.findByName(updateTeamDTO.getName())).thenReturn(team);

        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateTeamDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(expectedMessage));
    }
}
