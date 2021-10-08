package mil.af.abms.midas.api.performancemeasure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.capability.Capability;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.performancemeasure.dto.CreatePerformanceMeasureDTO;
import mil.af.abms.midas.api.performancemeasure.dto.UpdatePerformanceMeasureDTO;

@WebMvcTest({PerformanceMeasureController.class})
class PerformanceMeasureControllerTests extends ControllerTestHarness {

    @MockBean
    private PerformanceMeasureService performanceMeasureService;

    private final UpdatePerformanceMeasureDTO updatePerformanceMeasureDTO = new UpdatePerformanceMeasureDTO(1L, "title", 0);
    private final CreatePerformanceMeasureDTO createPerformanceMeasureDTO = new CreatePerformanceMeasureDTO("title", 0, 2L);
    private final PerformanceMeasure performanceMeasure = Builder.build(PerformanceMeasure.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setTitle(createPerformanceMeasureDTO.getTitle()))
            .with(p -> p.setCapability(Builder.build(Capability.class).with(c -> c.setId(2L)).get()))
            .with(p -> p.setIsArchived(false))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_performanceMeasure() throws Exception {
        when(performanceMeasureService.create(any(CreatePerformanceMeasureDTO.class))).thenReturn(performanceMeasure);

        mockMvc.perform(post("/api/performancemeasures")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createPerformanceMeasureDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(performanceMeasure.getTitle()))
                .andExpect(jsonPath("$.capabilityId").value(performanceMeasure.getCapability().getId()));
    }

    @Test
    void should_update_performanceMeasure_by_id() throws Exception {
        when(performanceMeasureService.updateById(anyLong(), any(UpdatePerformanceMeasureDTO.class))).thenReturn(performanceMeasure);

        mockMvc.perform(put("/api/performancemeasures/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updatePerformanceMeasureDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(performanceMeasure.getTitle()));
    }

    @Test
    void should_bulk_update_performanceMeasure() throws Exception {
        when(performanceMeasureService.bulkUpdate(any())).thenReturn(List.of(performanceMeasure));

        mockMvc.perform(put("/api/performancemeasures/bulk")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(List.of(updatePerformanceMeasureDTO)))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].title").value(performanceMeasure.getTitle()));
    }

    @Test
    public void should_update_performanceMeasure_is_archived_true() throws Exception {
        PerformanceMeasure archived = new PerformanceMeasure();
        BeanUtils.copyProperties(performanceMeasure, archived);
        archived.setIsArchived(true);

        IsArchivedDTO archiveDTO = new IsArchivedDTO(true);

        when(performanceMeasureService.updateIsArchived(1L, archiveDTO)).thenReturn(archived);

        mockMvc.perform(put("/api/performancemeasures/1/archive")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(archiveDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(true));
    }

}
