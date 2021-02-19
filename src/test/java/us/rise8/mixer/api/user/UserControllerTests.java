package us.rise8.mixer.api.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import us.rise8.mixer.api.ControllerTestHarness;
import us.rise8.mixer.api.helper.Builder;
import us.rise8.mixer.api.helper.JsonMapper;
import us.rise8.mixer.api.user.dto.UpdateUserDTO;
import us.rise8.mixer.api.user.dto.UpdateUserDisabledDTO;
import us.rise8.mixer.api.user.dto.UpdateUserRolesDTO;
import us.rise8.mixer.api.user.dto.UserDTO;
import us.rise8.mixer.exception.EntityNotFoundException;

@WebMvcTest({UserController.class})
public class UserControllerTests extends ControllerTestHarness {

    private final String USERNAME = "grogu";
    private final String UID = "abc-123";
    private final String EMAIL = "a.b@c";
    private final String DISPLAY_NAME = "baby yoda";
    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final UserDTO userDTO = Builder.build(UserDTO.class)
            .with(u -> u.setUsername(USERNAME))
            .with(u -> u.setEmail(EMAIL))
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid(UID))
            .with(u -> u.setDisplayName(DISPLAY_NAME))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setRoles(0L)).get();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;

    @BeforeEach
    public void init() throws Exception {
        UserModel authUser = Builder.build(UserModel.class)
                .with(u -> u.setRoles(1L))
                .with(u -> u.setKeycloakUid("abc-123"))
                .with(u -> u.setUsername("grogu")).get();
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void shouldGetUserById() throws Exception {
        when(userService.findById(any())).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.username").value(USERNAME));
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        UpdateUserDTO updateUserDTO = Builder.build(UpdateUserDTO.class)
                .with(u -> u.setUsername(USERNAME))
                .with(u -> u.setEmail("a.b@c"))
                .with(u -> u.setDisplayName("YoDiddy")).get();
        UserDTO updatedUserDTO = Builder.build(UserDTO.class)
                .with(u -> u.setId(1L))
                .with(u -> u.setKeycloakUid(UID))
                .with(u -> u.setUsername(USERNAME))
                .with(u -> u.setEmail(EMAIL))
                .with(u -> u.setDisplayName("YoDiddy"))
                .with(u -> u.setCreationDate(CREATION_DATE))
                .with(u -> u.setDodId(1L))
                .with(u -> u.setRoles(0L)).get();

        when(userService.findByUsername(any())).thenReturn(userDTO);
        when(userService.updateById(1L, updateUserDTO)).thenReturn(updatedUserDTO);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.displayName").value(updateUserDTO.getDisplayName()));
    }

    @Test
    public void shouldThrowUniqueNameExceptionOnUpdateUser() throws Exception {
        String expectedMessage = "username already in use";
        UpdateUserDTO updateUserDTO = Builder.build(UpdateUserDTO.class)
                .with(u -> u.setUsername(USERNAME))
                .with(u -> u.setEmail("a.b@c"))
                .with(u -> u.setDisplayName("YoDiddy")).get();
        UserDTO updatedUserDTO = Builder.build(UserDTO.class)
                .with(u -> u.setId(2L))
                .with(u -> u.setKeycloakUid(UID))
                .with(u -> u.setUsername(USERNAME)).get();

        when(userService.findByUsername(any())).thenReturn(updatedUserDTO);
        when(userService.updateById(1L, updateUserDTO)).thenReturn(updatedUserDTO);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(expectedMessage));
    }

    @Test
    public void shouldUpdateUserRoles() throws Exception {
        UpdateUserRolesDTO updateUserRolesDTO = Builder.build(UpdateUserRolesDTO.class)
                .with(p -> p.setRoles(1L)).get();
        UserDTO updatedUserDTO = new UserDTO(1L, UID, USERNAME, EMAIL, "YoDiddy", CREATION_DATE,
                1L, false, 0L);

        when(userService.updateRolesById(1L, updateUserRolesDTO)).thenReturn(updatedUserDTO);

        mockMvc.perform(put("/api/users/1/admin/roles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserRolesDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.roles").value(updatedUserDTO.getRoles()));
    }

    @Test
    public void shouldToggleUserDisabled() throws Exception {
        UpdateUserDisabledDTO updateUserDisabledDTO = Builder.build(UpdateUserDisabledDTO.class)
                .with(p -> p.setDisabled(true)).get();
        UserDTO updatedUserDTO = new UserDTO(1L, UID, USERNAME, EMAIL, "YoDiddy", CREATION_DATE,
                1L, true, 0L);

        when(userService.updateIsDisabledById(1L, updateUserDisabledDTO)).thenReturn(updatedUserDTO);

        mockMvc.perform(put("/api/users/1/admin/disable")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserDisabledDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isDisabled").value(updatedUserDTO.getIsDisabled()));
    }

    @Test
    public void shouldSearchUsers() throws Exception {
        List<UserModel> users = List.of(
                UserModel.fromDTO(userDTO),
                Builder.build(UserModel.class)
                        .with(u -> u.setId(2L))
                        .with(u -> u.setKeycloakUid("def-456"))
                        .with(u -> u.setUsername("yoda"))
                        .with(u -> u.setEmail("e.b@c"))
                        .with(u -> u.setDisplayName("baby yoda"))
                        .with(u -> u.setCreationDate(CREATION_DATE))
                        .with(u -> u.setDodId(1L))
                        .with(u -> u.setRoles(0L)).get()
        );
        Page<UserModel> page = new PageImpl<>(users);
        List<UserDTO> userDTOs = users.stream().map(UserModel::toDto).collect(Collectors.toList());

        when(userService.search(any(), any(), any(), any(), any())).thenReturn(page);
        when(userService.preparePageResponse(any(), any())).thenReturn(userDTOs);

        mockMvc.perform(get("/api/users?search=id>=1 AND user.username:\"yoda\" OR username:grogu"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(JsonMapper.dateMapper().writeValueAsString(userDTOs))
                );
    }

    @Test
    public void shouldThrowEntityNotFoundWhenIdNotFound() throws Exception {
        EntityNotFoundException expectedError = new EntityNotFoundException(UserModel.class.getSimpleName(), 1L);
        when(userService.findById(any())).thenThrow(expectedError);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()));
    }

    @Test
    public void shouldDeleteById() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(1L);
    }

}
