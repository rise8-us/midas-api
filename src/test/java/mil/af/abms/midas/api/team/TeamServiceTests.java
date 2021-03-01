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

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(TeamService.class)
public class TeamServiceTests {

    @Autowired
    TeamService teamService;

    @MockBean
    TeamRepository teamRepository;

    TeamEntity team = Builder.build(TeamEntity.class)
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setId(1L)).get();

    @Test
    public void should_Create_Team() {
        CreateTeamDTO createTeamDTO = new CreateTeamDTO("MIDAS", 2L);

        when(teamRepository.save(team)).thenReturn(new TeamEntity());

        teamService.create(createTeamDTO);

        verify(teamRepository, times(1)).save(team);
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
        UpdateTeamDTO updateTeamDTO = new UpdateTeamDTO(team.getName(), team.getIsArchived(), team.getGitlabGroupId());
        TeamDTO expectedDTO = team.toDto();
        expectedDTO.setId(1L);

        TeamEntity savedTeam = TeamEntity.fromDTO(expectedDTO);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.save(team)).thenReturn(team);

        teamService.updateById(1L, updateTeamDTO);

        verify(teamRepository, times(1)).save(savedTeam);
    }
}
