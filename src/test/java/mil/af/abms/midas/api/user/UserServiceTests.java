package mil.af.abms.midas.api.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.search.SpecificationsBuilder;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.dto.UpdateUserDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserDisabledDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserRolesDTO;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.config.CustomProperty;
import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationToken;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(UserService.class)
public class UserServiceTests {

    @Autowired
    UserService userService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    CustomProperty property;
    @MockBean
    TeamService teamService;
    @MockBean
    SecurityContext securityContext;

    @Captor
    ArgumentCaptor<User> userCaptor;

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("home one"))
            .with(t -> t.setGitlabGroupId(42L)).get();
    private final User expectedUser = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("grogu"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("Baby Yoda"))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setIsDisabled(false))
            .with(u -> u.setCreationDate(CREATION_DATE)).get();
    private final User expectedUser2 = Builder.build(User.class)
            .with(u -> u.setId(2L))
            .with(u -> u.setKeycloakUid("def-456"))
            .with(u -> u.setUsername("yoda"))
            .with(u -> u.setEmail("d.e@f"))
            .with(u -> u.setDisplayName("Yoda he is"))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setDodId(1L)).get();
    private final List<String> groups = List.of("midas-IL2-admin");
    private final PlatformOneAuthenticationToken token = new PlatformOneAuthenticationToken(
            "abc-123", 1L, "grogu", "a.b@c", groups);
    private final Authentication auth = new PlatformOneAuthenticationToken(expectedUser, null, new ArrayList<>());
    private final List<User> users = List.of(expectedUser, expectedUser2);
    private final Page<User> page = new PageImpl<User>(users);

    @Test
    public void should_create_user() {
        when(property.getJwtAdminGroup()).thenReturn("midas-IL2-admin");
        when(userRepository.save(any())).thenReturn(new User());

        userService.create(token);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userCaptured = userCaptor.getValue();

        assertThat(userCaptured.getKeycloakUid()).isEqualTo(token.getKeycloakUid());
        assertThat(userCaptured.getDodId()).isEqualTo(token.getDodId());
        assertThat(userCaptured.getDisplayName()).isEqualTo(token.getDisplayName());
        assertThat(userCaptured.getEmail()).isEqualTo(token.getEmail());
        assertThat(userCaptured.getRoles()).isEqualTo(1L);
    }

    @Test
    public void should_get_user_by_id() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));

        assertThat(userService.getObject(1L)).isEqualTo(expectedUser);
    }

    @Test
    public void should_get_user_by_username() throws EntityNotFoundException {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(expectedUser));

        assertThat(userService.findByUsername("foo")).isEqualTo(expectedUser);
    }

    @Test
    public void should_throw_exception_not_found_user_by_username() throws EntityNotFoundException {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                userService.findByUsername("foobar"));
        assertThat(e).hasMessage("Failed to find User by username: foobar");
    }

    @Test
    public void should_throw_error_when_id_null() throws EntityNotFoundException {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                userService.getObject(null));
        assertThat(e).hasMessage("Failed to find User");
    }

    @Test
    public void should_return_true_when_exists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(users.get(0)));

        assertTrue(userService.existsById(1L));
    }

    @Test
    public void should_return_false_when_exists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertFalse(userService.existsById(1L));
    }

    @Test
    public void should_throw_error_when_id_not_found() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> userService.getObject(1L));
        assertThat(e).hasMessage("Failed to find User with id 1");
    }

    @Test
    public void should_get_user_and_return_user() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(new User()));

        userService.findById(1L);

        verify(userRepository).findById(1L);
    }

    @Test
    public void should_update_user() {
        UpdateUserDTO updateDTO = Builder.build(UpdateUserDTO.class)
                .with(u -> u.setUsername("foobar"))
                .with(u -> u.setEmail("foo.bar@rise8.us"))
                .with(u -> u.setTeamIds(Set.of(1L)))
                .with(u -> u.setDisplayName("YoDiddy")).get();

        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));
        when(teamService.getObject(1L)).thenReturn(team);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        userService.updateById(1L, updateDTO);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userSaved = userCaptor.getValue();

        assertThat(userSaved.getDisplayName()).isEqualTo(updateDTO.getDisplayName());
        assertThat(userSaved.getUsername()).isEqualTo(updateDTO.getUsername());
        assertThat(userSaved.getEmail()).isEqualTo(updateDTO.getEmail());
        assertThat(userSaved.getTeams()).isEqualTo(Set.of(team));

    }

    @Test
    public void should_set_team_to_null() {
        UpdateUserDTO updateDTO = Builder.build(UpdateUserDTO.class)
                .with(u -> u.setTeamIds(Set.of())).get();

        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));
        when(teamService.getObject(1L)).thenReturn(team);

        userService.updateById(1L, updateDTO);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userSaved = userCaptor.getValue();

        assertThat(userSaved.getTeams()).isEqualTo(null);
    }

    @Test
    public void should_update_user_roles_by_id() {
        UpdateUserRolesDTO updateDTO = Builder.build(UpdateUserRolesDTO.class)
                .with(p -> p.setRoles(0L)).get();

        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        userService.updateRolesById(1L, updateDTO);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userSaved = userCaptor.getValue();

        assertThat(userSaved.getRoles()).isEqualTo(updateDTO.getRoles());
    }

    @Test
    public void should_update_is_disabled_by_id() {
        UpdateUserDisabledDTO updateDTO = Builder.build(UpdateUserDisabledDTO.class)
                .with(p -> p.setDisabled(true)).get();

        when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
        when(userRepository.save(any())).thenReturn(expectedUser);

        userService.updateIsDisabledById(1L, updateDTO);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userSaved = userCaptor.getValue();

        assertThat(userSaved.getIsDisabled()).isEqualTo(updateDTO.isDisabled());
    }

    @Test
    public void should_prepare_paged_response() {
        List<UserDTO> results = userService.preparePageResponse(page, new MockHttpServletResponse());

        assertThat(results).isEqualTo(users.stream().map(User::toDto).collect(Collectors.toList()));
    }

    @Test
    public void should_retrieve_all_users() {
        SpecificationsBuilder<User> builder = new SpecificationsBuilder<>();
        Specification<User> specs = builder.withSearch("id:1").build();

        when(userRepository.findAll(eq(specs), any(PageRequest.class))).thenReturn(page);

        assertThat(userService.search(specs, 1, null, null, null).stream().findFirst())
                .isEqualTo(Optional.of(users.get(0)));
    }

    @Test
    public void should_delete_by_id() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        userService.deleteById(1L);

        verify(userRepository, times(1)).delete(expectedUser);
    }

    @Test
    public void should_delete_all() {
        userService.deleteAll();

        verify(userRepository, times(1)).deleteAll();
    }

    @Test
    void should_get_user_by_keycloak_uid() {
        when(userRepository.findByKeycloakUid(any())).thenReturn(Optional.of(expectedUser));

        assertThat(userService.findByKeycloakUid("abc-123")).isEqualTo(Optional.of(expectedUser));
    }

    @Test
    void should_get_user_from_auth() {
        when(userRepository.findByKeycloakUid(expectedUser.getKeycloakUid())).thenReturn(Optional.of(expectedUser));

        User user = userService.getUserFromAuth(auth);

        assertThat(user.getKeycloakUid()).isEqualTo(expectedUser.getKeycloakUid());
    }

    @Test
    void should_throw_exception_on_get_user_from_auth() throws EntityNotFoundException {
        when(userRepository.findByKeycloakUid(expectedUser.getKeycloakUid())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserFromAuth(auth));
    }
}
