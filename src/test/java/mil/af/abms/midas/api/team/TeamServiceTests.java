package mil.af.abms.midas.api.team;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(TeamService.class)
class TeamServiceTests {

    @SpyBean
    TeamService teamService;
    @MockBean
    UserService userService;
    @MockBean
    SimpMessageSendingOperations websocket;
    @MockBean
    ProductService productService;
    @MockBean
    TeamRepository teamRepository;
    @Captor
    ArgumentCaptor<Team> teamCaptor;

    Product product = Builder.build(Product.class)
            .with(p -> p.setId(42L))
            .get();
    User user = Builder.build(User.class)
            .with(u -> u.setUsername("foo"))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setId(3L))
            .get();
    Team team = Builder.build(Team.class)
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setDescription("dev team"))
            .with(t -> t.setId(1L))
            .get();
    Set<User> users = Set.of(user);
    CreateTeamDTO createTeamDTO = Builder.build(CreateTeamDTO.class)
            .with(d -> d.setName("teamName"))
            .with(d -> d.setDescription("description"))
            .with(d -> d.setProductManagerId(3L))
            .with(d -> d.setDesignerId(3L))
            .with(d -> d.setTechLeadId(3L))
            .with(d -> d.setUserIds(Set.of(3L)))
            .with(d -> d.setPersonnelIds(Set.of()))
            .get();
    UpdateTeamDTO updateTeamDTO = Builder.build(UpdateTeamDTO.class)
            .with(d -> d.setName("teamName updated"))
            .with(d -> d.setDescription("description updated"))
            .with(d -> d.setProductManagerId(3L))
            .with(d -> d.setDesignerId(3L))
            .with(d -> d.setTechLeadId(3L))
            .with(d -> d.setUserIds(Set.of(3L)))
            .with(d -> d.setPersonnelIds(Set.of()))
            .get();

    @Test
    void should_create_team() {
        when(userService.findById(3L)).thenReturn(user);
        when(userService.findByIdOrNull(3L)).thenReturn(user);
        when(teamRepository.save(team)).thenReturn(new Team());
        when(productService.findById(42L)).thenReturn(product);

        teamService.create(createTeamDTO);

        verify(teamRepository, times(1)).save(teamCaptor.capture());
        assertThat(teamCaptor.getValue().getName()).isEqualTo(createTeamDTO.getName());
        assertThat(teamCaptor.getValue().getDescription()).isEqualTo(createTeamDTO.getDescription());
        assertThat(teamCaptor.getValue().getMembers()).isEqualTo(users);
        assertThat(teamCaptor.getValue().getProductManager()).isEqualTo(user);
        assertThat(teamCaptor.getValue().getDesigner()).isEqualTo(user);
        assertThat(teamCaptor.getValue().getTechLead()).isEqualTo(user);

    }

    @Test
    void should_find_by_name() throws EntityNotFoundException {
        when(teamRepository.findByName("MIDAS")).thenReturn(Optional.of(team));

        assertThat(teamService.findByName("MIDAS")).isEqualTo(team);
    }

    @Test
    void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () -> teamService.findByName("MIDAS"));
    }

    @Test
    void should_update_team_by_id() {
        when(userService.findById(3L)).thenReturn(user);
        when(userService.findByIdOrNull(3L)).thenReturn(user);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.save(team)).thenReturn(team);

        teamService.updateById(1L, updateTeamDTO);

        verify(teamRepository, times(1)).save(teamCaptor.capture());
        Team teamSaved = teamCaptor.getValue();

        assertThat(teamSaved.getName()).isEqualTo(updateTeamDTO.getName());
        assertThat(teamSaved.getDescription()).isEqualTo(updateTeamDTO.getDescription());
        assertThat(teamSaved.getProductManager()).isEqualTo(user);
        assertThat(teamSaved.getDesigner()).isEqualTo(user);
        assertThat(teamSaved.getTechLead()).isEqualTo(user);
    }

    @Test
    void should_update_is_archived_by_id() {
        IsArchivedDTO updateDTO = Builder.build(IsArchivedDTO.class)
                .with(d -> d.setIsArchived(true)).get();

        when(teamRepository.findById(any())).thenReturn(Optional.of(team));
        when(teamRepository.save(any())).thenReturn(team);

        teamService.updateIsArchivedById(1L, updateDTO);

        verify(teamRepository, times(1)).save(teamCaptor.capture());
        Team teamSaved = teamCaptor.getValue();

        assertThat(teamSaved.getIsArchived()).isEqualTo(updateDTO.getIsArchived());
    }
}
