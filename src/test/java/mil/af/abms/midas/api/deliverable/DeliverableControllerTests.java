package mil.af.abms.midas.api.deliverable;

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
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.deliverable.dto.CreateDeliverableDTO;
import mil.af.abms.midas.api.deliverable.dto.UpdateDeliverableDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.release.Release;
import mil.af.abms.midas.enums.ProgressionStatus;

@WebMvcTest({DeliverableController.class})
class DeliverableControllerTests extends ControllerTestHarness {

    @MockBean
    private DeliverableService deliverableService;

    private final UpdateDeliverableDTO updateDeliverableDTO = new UpdateDeliverableDTO(
            1L, "title", 1, 0, List.of(), ProgressionStatus.COMPLETED, 2L, 3L);
    private final CreateDeliverableDTO createDeliverableDTO = new CreateDeliverableDTO(
            "title", 1, 0, 2L, 3L, List.of(), List.of(10L),  9L, 5L, 2L, 6L
    );

    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(2L))
            .get();
    private final Release release = Builder.build(Release.class)
            .with(r -> r.setId(10L))
            .get();

    private final Deliverable deliverableUpdated = Builder.build(Deliverable.class)
            .with(t -> t.setId(updateDeliverableDTO.getId()))
            .with(t -> t.setPosition(updateDeliverableDTO.getIndex()))
            .with(t -> t.setStatus(updateDeliverableDTO.getStatus()))
            .with(t -> t.setTitle(updateDeliverableDTO.getTitle()))
            .get();
    private final Deliverable deliverableCreated = Builder.build(Deliverable.class)
            .with(t -> t.setPosition(createDeliverableDTO.getIndex()))
            .with(t -> t.setTitle(createDeliverableDTO.getTitle()))
            .with(t -> t.setProduct(product))
            .with(t -> t.setChildren(Set.of()))
            .with(t -> t.setReleases(Set.of(release)))
            .with(t -> t.setIsArchived(false))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_deliverable() throws Exception {
        when(deliverableService.create(any(CreateDeliverableDTO.class))).thenReturn(deliverableCreated);

        mockMvc.perform(post("/api/deliverables" +
                        "")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createDeliverableDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(deliverableCreated.getTitle()));
    }

    @Test
    void should_update_persona_by_id() throws Exception {
        when(deliverableService.updateById(anyLong(), any(UpdateDeliverableDTO.class))).thenReturn(deliverableUpdated);

        mockMvc.perform(put("/api/deliverables" +
                        "/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createDeliverableDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(deliverableUpdated.getTitle()));
    }


    @Test
    void should_bulk_update_persona() throws Exception {
        when(deliverableService.bulkUpdate(any())).thenReturn(List.of(deliverableCreated));

        mockMvc.perform(put("/api/deliverables" +
                        "/bulk")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(List.of(updateDeliverableDTO)))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].title").value(deliverableCreated.getTitle()));
    }

    @Test
    public void should_update_deliverable_is_archived_true() throws Exception {
        Deliverable archived = new Deliverable();
        BeanUtils.copyProperties(deliverableCreated, archived);
        archived.setIsArchived(true);

        IsArchivedDTO archiveDTO = new IsArchivedDTO(true);

        when(deliverableService.updateIsArchived(1L, archiveDTO)).thenReturn(archived);

        mockMvc.perform(put("/api/deliverables/1/archive")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(archiveDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(true));
    }

}
