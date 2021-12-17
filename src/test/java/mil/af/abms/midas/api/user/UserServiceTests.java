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

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
class UserServiceTests {

    @SpyBean
    UserService userService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    CustomProperty property;
    @MockBean
    TeamService teamService;

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
            .with(u -> u.setCompany("rise8"))
            .with(u -> u.setPhone("(555) 867-5309"))
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
    void should_create_user() {
        when(property.getJwtAdminGroup()).thenReturn("midas-IL2-admin");
        when(userRepository.save(any())).thenReturn(new User());

        userService.create(token);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userCaptured = userCaptor.getValue();

        assertThat(userCaptured.getKeycloakUid()).isEqualTo(token.getKeycloakUid());
        assertThat(userCaptured.getDodId()).isEqualTo(token.getDodId());
        assertThat(userCaptured.getDisplayName()).isEqualTo(token.getDisplayName());
        assertThat(userCaptured.getUsername()).isEqualTo(token.getDisplayName());
        assertThat(userCaptured.getEmail()).isEqualTo(token.getEmail());
        assertThat(userCaptured.getRoles()).isEqualTo(1L);
    }

    @Test
    void should_get_user_by_id() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));

        assertThat(userService.findById(1L)).isEqualTo(expectedUser);
    }

    @Test
    void should_get_user_by_username() throws EntityNotFoundException {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(expectedUser));

        assertThat(userService.findByUsername("foo")).isEqualTo(expectedUser);
    }

    @Test
    void should_throw_exception_not_found_user_by_username() throws EntityNotFoundException {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                userService.findByUsername("foobar"));
        assertThat(e).hasMessage("Failed to find User by username: foobar");
    }

    @Test
    void should_throw_error_when_id_null() throws EntityNotFoundException {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                userService.findById(null));
        assertThat(e).hasMessage("Failed to find User with id null");
    }

    @Test
    void should_return_true_when_exists() {
        when(userRepository.existsById(expectedUser.getId())).thenReturn(true);

        assertTrue(userService.existsById(1L));
    }

    @Test
    void should_return_false_when_exists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertFalse(userService.existsById(1L));
    }

    @Test
    void should_throw_error_when_id_not_found() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
        assertThat(e).hasMessage("Failed to find User with id 1");
    }

    @Test
    void should_get_user_and_return_user() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        userService.findById(1L);

        verify(userRepository).findById(1L);
    }

    @Test
    void should_update_user() {
        UpdateUserDTO updateDTO = Builder.build(UpdateUserDTO.class)
                .with(u -> u.setUsername("foobar"))
                .with(u -> u.setEmail("foo.bar@rise8.us"))
                .with(u -> u.setTeamIds(Set.of(1L)))
                .with(u -> u.setDisplayName("YoDiddy"))
                .with(u -> u.setCompany("rise8"))
                .with(u -> u.setPhone("(555) 867-5309")).get();

        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));
        when(teamService.findById(1L)).thenReturn(team);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        userService.updateById(1L, updateDTO);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userSaved = userCaptor.getValue();

        assertThat(userSaved.getDisplayName()).isEqualTo(updateDTO.getDisplayName());
        assertThat(userSaved.getUsername()).isEqualTo(updateDTO.getUsername());
        assertThat(userSaved.getEmail()).isEqualTo(updateDTO.getEmail());
        assertThat(userSaved.getTeams()).isEqualTo(Set.of(team));
        assertThat(userSaved.getPhone()).isEqualTo(updateDTO.getPhone());
        assertThat(userSaved.getCompany()).isEqualTo(updateDTO.getCompany());

    }

    @Test
    void should_set_team_to_empty_set() {
        UpdateUserDTO updateDTO = Builder.build(UpdateUserDTO.class)
                .with(u -> u.setTeamIds(Set.of())).get();

        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        userService.updateById(1L, updateDTO);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userSaved = userCaptor.getValue();

        assertThat(userSaved.getTeams()).isEqualTo(Set.of());
    }

    @Test
    void should_update_user_roles_by_id() {
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
    void should_update_is_disabled_by_id() {
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
    void should_prepare_paged_response() {
        List<UserDTO> results = userService.preparePageResponse(page, new MockHttpServletResponse());

        assertThat(results).isEqualTo(users.stream().map(User::toDto).collect(Collectors.toList()));
    }

    @Test
    void should_retrieve_all_users() {
        SpecificationsBuilder<User> builder = new SpecificationsBuilder<>();
        Specification<User> specs = builder.withSearch("id:1").build();

        when(userRepository.findAll(eq(specs), any(PageRequest.class))).thenReturn(page);

        assertThat(userService.search(specs, 1, null, null, null).stream().findFirst())
                .isEqualTo(Optional.of(users.get(0)));
    }

    @Test
    void should_delete_by_id() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        userService.deleteById(1L);

        verify(userRepository, times(1)).delete(expectedUser);
    }

    @Test
    void should_delete_all() {
        userService.deleteAll();

        verify(userRepository, times(1)).deleteAll();
    }

    @Test
    void should_get_user_by_keycloak_uid() {
        when(userRepository.findByKeycloakUid(any())).thenReturn(Optional.of(expectedUser));

        userService.getByKeycloakUid(expectedUser.getKeycloakUid());

        assertThat(userService.findByKeycloakUid("abc-123")).isEqualTo(Optional.of(expectedUser));
    }

    @Test
    void should_throw_get_user_by_keycloak_exception() {
        when(userRepository.findByKeycloakUid(any())).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> userService.getByKeycloakUid("123-zyx"));
        assertThat(e).hasMessage("Failed to find User by keycloakUid: 123-zyx");
    }

    @Test
    void should_get_user_by_security_context() {
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByKeycloakUid("abc-123")).thenReturn(Optional.of(expectedUser));

        assertThat(userService.getUserBySecContext()).isEqualTo(expectedUser);
    }

    @Test
    void should_return_user_by_id_or_null() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        assertThat(userService.findByIdOrNull(expectedUser.getId())).isEqualTo(expectedUser);
        assertThat(userService.findByIdOrNull(null)).isEqualTo(null);
    }

    @ParameterizedTest
    @CsvSource(value = {"true, false"})
    void should_return_user_displayName_or_username(boolean hasDisplayName) {
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findByKeycloakUid("abc-123")).thenReturn(Optional.of(expectedUser));

        expectedUser.setDisplayName(hasDisplayName ? expectedUser.getDisplayName() : null);

        assertThat(userService.getUserDisplayNameOrUsername()).isEqualTo(hasDisplayName ? expectedUser.getDisplayName() : expectedUser.getUsername());
    }

}
