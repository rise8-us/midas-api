package mil.af.abms.midas.api.team;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

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
import mil.af.abms.midas.api.team.dto.UpdateTeamIsArchivedDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(TeamService.class)
public class TeamServiceTests {

    @Autowired
    TeamService teamService;
    @MockBean
    UserService userService;
    @MockBean
    TeamRepository teamRepository;
    @Captor
    ArgumentCaptor<Team> teamCaptor;

    User user = Builder.build(User.class)
            .with(u -> u.setUsername("foo"))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setId(3L)).get();
    Team team = Builder.build(Team.class)
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setDescription("dev team"))
            .with(t -> t.setId(1L)).get();
    Set<User> users = Set.of(user);

    @Test
    public void should_create_team() {
        CreateTeamDTO createTeamDTO = new CreateTeamDTO("MIDAS", 2L, "dev team", Set.of(3L));

        when(userService.getObject(3L)).thenReturn(user);
        when(teamRepository.save(team)).thenReturn(new Team());

        teamService.create(createTeamDTO);

        verify(teamRepository, times(1)).save(teamCaptor.capture());
        Team teamSaved = teamCaptor.getValue();

        assertThat(teamSaved.getName()).isEqualTo(createTeamDTO.getName());
        assertThat(teamSaved.getGitlabGroupId()).isEqualTo(createTeamDTO.getGitlabGroupId());
        assertThat(teamSaved.getDescription()).isEqualTo(createTeamDTO.getDescription());
        assertThat(teamSaved.getUsers()).isEqualTo(users);
    }

    @Test
    public void should_find_by_name() throws EntityNotFoundException {
        when(teamRepository.findByName("MIDAS")).thenReturn(Optional.of(team));

        assertThat(teamService.findByName("MIDAS")).isEqualTo(team);
    }

    @Test
    public void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                teamService.findByName("MIDAS"));
    }

    @Test
    public void should_update_team_by_id() {
        UpdateTeamDTO updateTeamDTO = new UpdateTeamDTO("Home One", 22L, "dev team", Set.of(3L));

        when(userService.getObject(3L)).thenReturn(user);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.save(team)).thenReturn(team);

        teamService.updateById(1L, updateTeamDTO);

        verify(teamRepository, times(1)).save(teamCaptor.capture());
        Team teamSaved = teamCaptor.getValue();

        assertThat(teamSaved.getName()).isEqualTo(updateTeamDTO.getName());
        assertThat(teamSaved.getGitlabGroupId()).isEqualTo(updateTeamDTO.getGitlabGroupId());
        assertThat(teamSaved.getDescription()).isEqualTo(updateTeamDTO.getDescription());
    }

    @Test
    public void should_update_is_archived_by_id() {
        UpdateTeamIsArchivedDTO updateDTO = Builder.build(UpdateTeamIsArchivedDTO.class)
                .with(d -> d.setIsArchived(true)).get();

        when(teamRepository.findById(any())).thenReturn(Optional.of(team));
        when(teamRepository.save(any())).thenReturn(team);

        teamService.updateIsArchivedById(1L, updateDTO);

        verify(teamRepository, times(1)).save(teamCaptor.capture());
        Team teamSaved = teamCaptor.getValue();

        assertThat(teamSaved.getIsArchived()).isEqualTo(updateDTO.getIsArchived());
    }
}
