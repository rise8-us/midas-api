package mil.af.abms.midas.api.capability;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.capability.dto.CreateCapabilityDTO;
import mil.af.abms.midas.api.capability.dto.UpdateCapabilityDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;

@WebMvcTest({CapabilityController.class})
public class CapabilityControllerTests extends ControllerTestHarness {

    @MockBean
    private CapabilityService capabilityService;

    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final UpdateCapabilityDTO updateCapabilityDTO = new UpdateCapabilityDTO("title", "description", 0);
    private final CreateCapabilityDTO createCapabilityDTO = new CreateCapabilityDTO("title", "description", 1);
    private final Capability capability = Builder.build(Capability.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setTitle(createCapabilityDTO.getTitle()))
            .with(p -> p.setDescription(createCapabilityDTO.getDescription()))
            .with(p -> p.setIsArchived(false))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_capability() throws Exception {
        when(capabilityService.create(any(CreateCapabilityDTO.class))).thenReturn(capability);

        mockMvc.perform(post("/api/capabilities")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createCapabilityDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(capability.getTitle()));
    }

    @Test
    void should_update_capability_by_id() throws Exception {
        when(capabilityService.updateById(anyLong(), any(UpdateCapabilityDTO.class))).thenReturn(capability);

        mockMvc.perform(put("/api/capabilities/2")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(updateCapabilityDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(capability.getTitle()));
    }

    @Test
    public void should_update_capability_is_archived_true() throws Exception {
        Capability archived = new Capability();
        BeanUtils.copyProperties(capability, archived);
        archived.setIsArchived(true);

        IsArchivedDTO archiveDTO = new IsArchivedDTO(true);

        when(capabilityService.updateIsArchived(1L, archiveDTO)).thenReturn(archived);

        mockMvc.perform(put("/api/capabilities/1/archive")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(archiveDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(true));
    }

    @Test
    void should_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/capabilities/1"))
                .andExpect(status().isOk());

        verify(capabilityService, times(1)).deleteById(1L);
    }
}
