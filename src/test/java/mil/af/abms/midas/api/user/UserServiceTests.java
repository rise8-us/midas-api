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

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.search.SpecificationsBuilder;
import mil.af.abms.midas.api.user.dto.UpdateUserDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserDisabledDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserRolesDTO;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationToken;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(UserService.class)
public class UserServiceTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final UserEntity expectedUser = Builder.build(UserEntity.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("grogu"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("Baby Yoda"))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setCreationDate(CREATION_DATE)).get();
    private final UserEntity expectedUser2 = Builder.build(UserEntity.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("def-456"))
            .with(u -> u.setUsername("yoda"))
            .with(u -> u.setEmail("d.e@f"))
            .with(u -> u.setDisplayName("Yoda he is"))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setDodId(1L)).get();
    private final List<String> groups = List.of("ADMIN");
    private final PlatformOneAuthenticationToken token = new PlatformOneAuthenticationToken(
            "abc-123", 1L, "grogu", "a.b@c", groups);
    private final List<UserEntity> users = List.of(expectedUser, expectedUser2);
    private final Page<UserEntity> page = new PageImpl<UserEntity>(users);
    @Autowired
    UserService userService;
    @MockBean
    UserRepository userRepository;

    @Test
    public void should_Create_User() {
        UserEntity tokenUser = Builder.build(UserEntity.class)
                .with(u -> u.setKeycloakUid("abc-123"))
                .with(u -> u.setDodId(1L))
                .with(u -> u.setUsername("grogu"))
                .with(u -> u.setEmail("a.b@c"))
                .with(u -> u.setRoles(1L)).get();

        when(userRepository.save(any())).thenReturn(new UserEntity());

        userService.create(token);

        verify(userRepository, times(1)).save(tokenUser);

    }

    @Test
    public void should_Get_User_By_Id() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));

        assertThat(userService.getObject(1L)).isEqualTo(expectedUser);
    }

    @Test
    public void should_Get_User_By_Username() throws EntityNotFoundException {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(expectedUser));

        assertThat(userService.findByUsername("foo")).isEqualTo(expectedUser.toDto());
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
    public void should_Get_User_And_Return_UserDTO() throws EntityNotFoundException {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(new UserEntity()));

        assertThat(userService.findById(1L).getClass()).isEqualTo(UserDTO.class);
    }

    @Test
    public void should_Update_User() {
        UpdateUserDTO updateDTO = Builder.build(UpdateUserDTO.class)
                .with(u -> u.setUsername(expectedUser.getUsername()))
                .with(u -> u.setEmail(expectedUser.getEmail()))
                .with(u -> u.setDisplayName("YoDiddy")).get();
        UserDTO expectedDTO = expectedUser.toDto();
        expectedDTO.setDisplayName("YoDiddy");
        UserEntity savedUser = UserEntity.fromDTO(expectedDTO);

        when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
        when(userRepository.save(any())).thenReturn(expectedUser);

        userService.updateById(1L, updateDTO);

        verify(userRepository, times(1)).save(savedUser);
    }

    @Test
    public void should_Update_User_Roles_By_Id() {
        UpdateUserRolesDTO updateDTO = Builder.build(UpdateUserRolesDTO.class)
                .with(p -> p.setRoles(0L)).get();
        UserDTO expectedDTO = expectedUser.toDto();
        expectedDTO.setRoles(1L);
        UserEntity savedUser = UserEntity.fromDTO(expectedDTO);

        when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
        when(userRepository.save(any())).thenReturn(expectedUser);

        userService.updateRolesById(1L, updateDTO);

        verify(userRepository, times(1)).save(savedUser);
    }

    @Test
    public void should_Update_Is_Disabled_By_Id() {
        UpdateUserDisabledDTO updateDTO = Builder.build(UpdateUserDisabledDTO.class)
                .with(p -> p.setDisabled(true)).get();
        UserDTO expectedDTO = expectedUser.toDto();
        expectedDTO.setIsDisabled(true);
        UserEntity savedUser = UserEntity.fromDTO(expectedDTO);

        when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
        when(userRepository.save(any())).thenReturn(expectedUser);

        userService.updateIsDisabledById(1L, updateDTO);

        verify(userRepository, times(1)).save(savedUser);
    }

    @Test
    public void should_Prepare_Paged_Response() {
        List<UserDTO> results = userService.preparePageResponse(page, new MockHttpServletResponse());

        assertThat(results).isEqualTo(users.stream().map(UserEntity::toDto).collect(Collectors.toList()));
    }

    @Test
    public void should_Retrieve_All_Users() {
        SpecificationsBuilder<UserEntity> builder = new SpecificationsBuilder<>();
        Specification<UserEntity> specs = builder.withSearch("id:1").build();

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
