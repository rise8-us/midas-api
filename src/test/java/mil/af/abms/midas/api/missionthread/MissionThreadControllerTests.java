package mil.af.abms.midas.api.missionthread;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.missionthread.dto.CreateMissionThreadDTO;
import mil.af.abms.midas.api.missionthread.dto.UpdateMissionThreadDTO;

@WebMvcTest({MissionThreadController.class})
class MissionThreadControllerTests extends ControllerTestHarness {

    @MockBean
    private MissionThreadService missionThreadService;

    private final UpdateMissionThreadDTO updateMissionThreadDTO = new UpdateMissionThreadDTO("title");
    private final CreateMissionThreadDTO createMissionThreadDTO = new CreateMissionThreadDTO("title");
    private final MissionThread missionThread = Builder.build(MissionThread.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setTitle(createMissionThreadDTO.getTitle()))
            .with(t -> t.setIsArchived(false))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_missionThread() throws Exception {
        when(missionThreadService.create(any(CreateMissionThreadDTO.class))).thenReturn(missionThread);

        mockMvc.perform(post("/api/missionthreads")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createMissionThreadDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(missionThread.getTitle()));
    }

    @Test
    void should_update_missionThread_by_id() throws Exception {
        when(missionThreadService.updateById(anyLong(), any(UpdateMissionThreadDTO.class))).thenReturn(missionThread);

        mockMvc.perform(put("/api/missionthreads/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateMissionThreadDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(missionThread.getTitle()));
    }

    @Test
    public void should_update_missionThread_is_archived_true() throws Exception {
        MissionThread archived = new MissionThread();
        BeanUtils.copyProperties(missionThread, archived);
        archived.setIsArchived(true);

        IsArchivedDTO archiveDTO = new IsArchivedDTO(true);

        when(missionThreadService.updateIsArchived(1L, archiveDTO)).thenReturn(archived);

        mockMvc.perform(put("/api/missionthreads/1/archive")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(archiveDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(true));
    }

}
