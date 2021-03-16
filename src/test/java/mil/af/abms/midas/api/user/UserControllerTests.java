package mil.af.abms.midas.api.user;

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

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.api.user.dto.UpdateUserDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserDisabledDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserRolesDTO;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({UserController.class})
public class UserControllerTests extends ControllerTestHarness {

    private final static String USERNAME = "grogu";
    private final static String UID = "abc-123";
    private final static String EMAIL = "a.b@c";
    private final static String DISPLAY_NAME = "baby yoda";
    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final User user = Builder.build(User.class)
            .with(u -> u.setUsername(USERNAME))
            .with(u -> u.setEmail(EMAIL))
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid(UID))
            .with(u -> u.setDisplayName(DISPLAY_NAME))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setRoles(0L)).get();
    private final User user2 = Builder.build(User.class)
            .with(u -> u.setId(2L))
            .with(u -> u.setKeycloakUid("def-456"))
            .with(u -> u.setUsername("yoda"))
            .with(u -> u.setEmail("e.b@c"))
            .with(u -> u.setDisplayName("baby yoda"))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setRoles(0L)).get();
    private final UserDTO userDTO = user.toDto();
    private final UpdateUserDTO updateUserDTO = Builder.build(UpdateUserDTO.class)
            .with(u -> u.setUsername(USERNAME))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("YoDiddy")).get();
    private final List<User> users = List.of(user, user2);

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_get_user_by_id() throws Exception {
        when(userService.findById(any())).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.username").value(USERNAME));
    }

    @Test
    public void should_update_user() throws Exception {
       user.setDisplayName("YoDiddy");

        when(userService.findByUsername(any())).thenReturn(user);
        when(userService.updateById(1L, updateUserDTO)).thenReturn(user);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.displayName").value(updateUserDTO.getDisplayName()));
    }

    @Test
    public void should_throw_unique_name_exception_on_update_user() throws Exception {
        String expectedMessage = "username already in use";

        when(userService.findByUsername(any())).thenReturn(user);

        mockMvc.perform(put("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(expectedMessage));
    }
    
    @Test
    public void should_update_user_roles() throws Exception {
        UpdateUserRolesDTO updateUserRolesDTO = Builder.build(UpdateUserRolesDTO.class)
                .with(p -> p.setRoles(1L)).get();
        UserDTO userDTOUpdated = user.toDto();
        userDTOUpdated.setRoles(0L);

        when(userService.updateRolesById(1L, updateUserRolesDTO)).thenReturn(user);

        mockMvc.perform(put("/api/users/1/admin/roles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserRolesDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.roles").value(userDTOUpdated.getRoles()));
    }

    @Test
    public void should_toggle_user_disabled() throws Exception {
        UpdateUserDisabledDTO updateUserDisabledDTO = Builder.build(UpdateUserDisabledDTO.class)
                .with(p -> p.setDisabled(true)).get();
        user.setIsDisabled(true);

        when(userService.updateIsDisabledById(1L, updateUserDisabledDTO)).thenReturn(user);

        mockMvc.perform(put("/api/users/1/admin/disable")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserDisabledDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isDisabled").value(user.getIsDisabled()));
    }

    @Test
    public void should_search_users() throws Exception {
        Page<User> page = new PageImpl<>(users);
        List<UserDTO> userDTOs = users.stream().map(User::toDto).collect(Collectors.toList());

        when(userService.search(any(), any(), any(), any(), any())).thenReturn(page);
        when(userService.preparePageResponse(any(), any())).thenReturn(userDTOs);

        mockMvc.perform(get("/api/users?search=id>=1 AND user.username:\"yoda\" OR username:grogu"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(JsonMapper.dateMapper().writeValueAsString(userDTOs))
                );
    }

    @Test
    public void should_throw_entityNotFound_when_id_not_found() throws Exception {
        EntityNotFoundException expectedError = new EntityNotFoundException(User.class.getSimpleName(), 1L);
        when(userService.findById(any())).thenThrow(expectedError);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()));
    }

    @Test
    public void should_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(1L);
    }

}
