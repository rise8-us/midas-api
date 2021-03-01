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

    private final UserEntity user = Builder.build(UserEntity.class)
            .with(u -> u.setUsername(USERNAME))
            .with(u -> u.setEmail(EMAIL))
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid(UID))
            .with(u -> u.setDisplayName(DISPLAY_NAME))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setRoles(0L)).get();
    private final UserEntity user2 = Builder.build(UserEntity.class)
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
    private final List<UserEntity> users = List.of(user, user2);

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_Get_User_By_Id() throws Exception {
        when(userService.findById(any())).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.username").value(USERNAME));
    }

    @Test
    public void should_Update_User() throws Exception {
        UserDTO UserDTOUpdated = user.toDto();
        UserDTOUpdated.setDisplayName("YoDiddy");

        when(userService.findByUsername(any())).thenReturn(userDTO);
        when(userService.updateById(1L, updateUserDTO)).thenReturn(UserDTOUpdated);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.displayName").value(updateUserDTO.getDisplayName()));
    }

    @Test
    public void should_Throw_Unique_Name_Exception_On_Update_User() throws Exception {
        String expectedMessage = "username already in use";
        UserDTO UserDTOExisting = user2.toDto();
        UserDTOExisting.setId(2L);

        when(userService.findByUsername(any())).thenReturn(UserDTOExisting);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(expectedMessage));
    }
    
    @Test
    public void should_Update_User_Roles() throws Exception {
        UpdateUserRolesDTO updateUserRolesDTO = Builder.build(UpdateUserRolesDTO.class)
                .with(p -> p.setRoles(1L)).get();
        UserDTO userDTOUpdated = user.toDto();
        userDTOUpdated.setRoles(0L);

        when(userService.updateRolesById(1L, updateUserRolesDTO)).thenReturn(userDTOUpdated);

        mockMvc.perform(put("/api/users/1/admin/roles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserRolesDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.roles").value(userDTOUpdated.getRoles()));
    }

    @Test
    public void should_Toggle_User_Disabled() throws Exception {
        UpdateUserDisabledDTO updateUserDisabledDTO = Builder.build(UpdateUserDisabledDTO.class)
                .with(p -> p.setDisabled(true)).get();
        UserDTO userDTOUpdated = user.toDto();
        userDTOUpdated.setIsDisabled(true);

        when(userService.updateIsDisabledById(1L, updateUserDisabledDTO)).thenReturn(userDTOUpdated);

        mockMvc.perform(put("/api/users/1/admin/disable")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateUserDisabledDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isDisabled").value(userDTOUpdated.getIsDisabled()));
    }

    @Test
    public void should_Search_Users() throws Exception {
        Page<UserEntity> page = new PageImpl<>(users);
        List<UserDTO> userDTOs = users.stream().map(UserEntity::toDto).collect(Collectors.toList());

        when(userService.search(any(), any(), any(), any(), any())).thenReturn(page);
        when(userService.preparePageResponse(any(), any())).thenReturn(userDTOs);

        mockMvc.perform(get("/api/users?search=id>=1 AND user.username:\"yoda\" OR username:grogu"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(JsonMapper.dateMapper().writeValueAsString(userDTOs))
                );
    }

    @Test
    public void should_Throw_EntityNotFound_When_Id_Not_Found() throws Exception {
        EntityNotFoundException expectedError = new EntityNotFoundException(UserEntity.class.getSimpleName(), 1L);
        when(userService.findById(any())).thenThrow(expectedError);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()));
    }

    @Test
    public void should_Delete_By_Id() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(1L);
    }

}
