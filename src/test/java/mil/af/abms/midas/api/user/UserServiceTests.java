package mil.af.abms.midas.api.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.search.SpecificationsBuilder;
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

    @Captor
    ArgumentCaptor<User> userCaptor;

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
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
    private final List<User> users = List.of(expectedUser, expectedUser2);
    private final Page<User> page = new PageImpl<User>(users);

    @Test
    public void should_Create_User() {
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
    public void should_Get_User_By_Id() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));

        assertThat(userService.getObject(1L)).isEqualTo(expectedUser);
    }

    @Test
    public void should_Get_User_By_Username() throws EntityNotFoundException {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(expectedUser));

        assertThat(userService.findByUsername("foo")).isEqualTo(expectedUser);
    }

    @Test
    public void should_Throw_Exception_Not_Found_User_By_Username() throws EntityNotFoundException {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                userService.findByUsername("foobar"));
        assertThat(e).hasMessage("Failed to find User by username: foobar");
    }

    @Test
    public void should_Throw_Error_When_Id_Null() throws EntityNotFoundException {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                userService.getObject(null));
        assertThat(e).hasMessage("Failed to find User");
    }

    @Test
    public void should_Return_True_When_Exists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(users.get(0)));

        assertTrue(userService.existsById(1L));
    }

    @Test
    public void should_Return_False_When_Exists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertFalse(userService.existsById(1L));
    }

    @Test
    public void should_Throw_Error_When_Id_Not_Found() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> userService.getObject(1L));
        assertThat(e).hasMessage("Failed to find User with id 1");
    }

    @Test
    public void should_Get_User_And_Return_User() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(new User()));

        userService.findById(1L);

        verify(userRepository).findById(1L);
    }

    @Test
    public void should_Update_User() {
        UpdateUserDTO updateDTO = Builder.build(UpdateUserDTO.class)
                .with(u -> u.setUsername("foobar"))
                .with(u -> u.setEmail("foo.bar@rise8.us"))
                .with(u -> u.setDisplayName("YoDiddy")).get();

        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));
        when(userRepository.save(any(User.class))).thenReturn(new User());

        userService.updateById(1L, updateDTO);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userSaved = userCaptor.getValue();

        assertThat(userSaved.getDisplayName()).isEqualTo(updateDTO.getDisplayName());
        assertThat(userSaved.getUsername()).isEqualTo(updateDTO.getUsername());
        assertThat(userSaved.getEmail()).isEqualTo(updateDTO.getEmail());
    }

    @Test
    public void should_Update_User_Roles_By_Id() {
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
    public void should_Update_Is_Disabled_By_Id() {
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
    public void should_Prepare_Paged_Response() {
        List<UserDTO> results = userService.preparePageResponse(page, new MockHttpServletResponse());

        assertThat(results).isEqualTo(users.stream().map(User::toDto).collect(Collectors.toList()));
    }

    @Test
    public void should_Retrieve_All_Users() {
        SpecificationsBuilder<User> builder = new SpecificationsBuilder<>();
        Specification<User> specs = builder.withSearch("id:1").build();

        when(userRepository.findAll(eq(specs), any(PageRequest.class))).thenReturn(page);

        assertThat(userService.search(specs, 1, null, null, null).stream().findFirst())
                .isEqualTo(Optional.of(users.get(0)));
    }

    @Test
    public void should_Delete_By_Id() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        userService.deleteById(1L);

        verify(userRepository, times(1)).delete(expectedUser);
    }

    @Test
    public void should_Delete_All() {
        userService.deleteAll();

        verify(userRepository, times(1)).deleteAll();
    }

    @Test
    void should_Get_User_By_Keycloak_Uid() {
        when(userRepository.findByKeycloakUid(any())).thenReturn(Optional.of(expectedUser));

        assertThat(userService.findByKeycloakUid("abc-123")).isEqualTo(Optional.of(expectedUser));
    }
}
