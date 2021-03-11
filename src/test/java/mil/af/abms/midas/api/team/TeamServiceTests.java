package mil.af.abms.midas.api.team;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(TeamService.class)
public class TeamServiceTests {

    @Autowired
    TeamService teamService;
    @MockBean
    TeamRepository teamRepository;
    @Captor
    ArgumentCaptor<Team> teamCaptor;

    Team team = Builder.build(Team.class)
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setDescription("dev team"))
            .with(t -> t.setId(1L)).get();

    @Test
    public void should_Create_Team() {
        CreateTeamDTO createTeamDTO = new CreateTeamDTO("MIDAS", 2L, "dev team");

        when(teamRepository.save(team)).thenReturn(new Team());

        teamService.create(createTeamDTO);

        verify(teamRepository, times(1)).save(teamCaptor.capture());
        Team teamSaved = teamCaptor.getValue();

        assertThat(teamSaved.getName()).isEqualTo(createTeamDTO.getName());
        assertThat(teamSaved.getGitlabGroupId()).isEqualTo(createTeamDTO.getGitlabGroupId());
        assertThat(teamSaved.getDescription()).isEqualTo(createTeamDTO.getDescription());
    }

    @Test
    public void should_Find_By_Name() throws EntityNotFoundException {
        when(teamRepository.findByName("MIDAS")).thenReturn(Optional.of(team));

        assertThat(teamService.findByName("MIDAS")).isEqualTo(team);
    }

    @Test
    public void should_Throw_Error_Find_By_Name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                teamService.findByName("MIDAS"));
    }

    @Test
    public void should_Update_Team_By_Id() {
        UpdateTeamDTO updateTeamDTO = new UpdateTeamDTO("Home One", true, 22L, "dev team");

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.save(team)).thenReturn(team);

        teamService.updateById(1L, updateTeamDTO);

        verify(teamRepository, times(1)).save(teamCaptor.capture());
        Team teamSaved = teamCaptor.getValue();

        assertThat(teamSaved.getName()).isEqualTo(updateTeamDTO.getName());
        assertThat(teamSaved.getGitlabGroupId()).isEqualTo(updateTeamDTO.getGitlabGroupId());
        assertThat(teamSaved.getIsArchived()).isEqualTo(updateTeamDTO.getIsArchived());
        assertThat(teamSaved.getDescription()).isEqualTo(updateTeamDTO.getDescription());
    }
}
