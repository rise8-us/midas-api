package mil.af.abms.midas.api.deliverable;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.capability.Capability;
import mil.af.abms.midas.api.capability.CapabilityService;
import mil.af.abms.midas.api.completion.Completion;
import mil.af.abms.midas.api.completion.CompletionService;
import mil.af.abms.midas.api.completion.dto.CreateCompletionDTO;
import mil.af.abms.midas.api.completion.dto.UpdateCompletionDTO;
import mil.af.abms.midas.api.deliverable.dto.CreateDeliverableDTO;
import mil.af.abms.midas.api.deliverable.dto.UpdateDeliverableDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.performancemeasure.PerformanceMeasure;
import mil.af.abms.midas.api.performancemeasure.PerformanceMeasureService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.CompletionType;
import mil.af.abms.midas.enums.ProgressionStatus;

@ExtendWith(SpringExtension.class)
@Import(DeliverableService.class)
class DeliverableServiceTests {

    @SpyBean
    DeliverableService deliverableService;
    @MockBean
    ProductService productService;
    @MockBean
    UserService userService;
    @MockBean
    EpicService epicService;
    @MockBean
    CapabilityService capabilityService;
    @MockBean
    CompletionService completionService;
    @MockBean
    PerformanceMeasureService performanceMeasureService;
    @MockBean
    DeliverableRepository deliverableRepository;
    @Captor
    ArgumentCaptor<Deliverable> deliverableCaptor;
    @MockBean
    SimpMessageSendingOperations websocket;

    private final User assignedTo = Builder.build(User.class).with(u -> u.setId(2L)).get();
    private final Product product = Builder.build(Product.class).with(p -> p.setId(4L)).get();
    private final Capability capability = Builder.build(Capability.class)
            .with(c -> c.setId(5L))
            .get();
    private final PerformanceMeasure performanceMeasure = Builder.build(PerformanceMeasure.class).with(p -> p.setId(5L)).get();
    private final CreateCompletionDTO createCompletionDTO = new CreateCompletionDTO();
    private final Completion completion = Builder.build(Completion.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setValue(0F))
            .with(c -> c.setTarget(1F))
            .with(c -> c.setCompletionType(CompletionType.BINARY))
            .with(c -> c.setDueDate(null))
            .with(c -> c.setStartDate(null))
            .get();
    private final UpdateCompletionDTO updateCompletionDTO = new UpdateCompletionDTO(
            null,
            null,
            CompletionType.BINARY,
            0F,
            1F,
            null,
            null
    );
    private final Deliverable deliverable = Builder.build(Deliverable.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setTitle("title"))
            .with(d -> d.setProduct(product))
            .with(d -> d.setChildren(Set.of()))
            .with(d -> d.setStatus(ProgressionStatus.NOT_STARTED))
            .with(d -> d.setPosition(0))
            .with(d -> d.setReferenceId(1))
            .with(d -> d.setPerformanceMeasure(performanceMeasure))
            .with(d -> d.setAssignedTo(assignedTo))
            .with(d -> d.setCapability(capability))
            .with(d -> d.setIsArchived(false))
            .with(d -> d.setCompletion(completion))
            .get();

    @Test
    void should_create_deliverable() {
        CreateDeliverableDTO createDeliverableDTO = new CreateDeliverableDTO(
                "title", 1, 0, 4L, null, List.of(), 9L, 5L, 2L, createCompletionDTO);

        when(productService.findByIdOrNull(createDeliverableDTO.getProductId())).thenReturn(product);
        when(performanceMeasureService.findByIdOrNull(createDeliverableDTO.getPerformanceMeasureId())).thenReturn(performanceMeasure);
        when(deliverableRepository.save(deliverable)).thenReturn(new Deliverable());
        when(userService.findByIdOrNull(createDeliverableDTO.getAssignedToId())).thenReturn(assignedTo);
        when(capabilityService.findByIdOrNull(capability.getId())).thenReturn(capability);
        doNothing().when(deliverableService).updateParentCompletion(anyLong(), anyFloat());

        deliverableService.create(createDeliverableDTO);

        verify(deliverableRepository, times(1)).save(deliverableCaptor.capture());
        Deliverable deliverableSaved = deliverableCaptor.getValue();

        assertThat(deliverableSaved.getTitle()).isEqualTo(createDeliverableDTO.getTitle());
        assertThat(deliverableSaved.getPosition()).isEqualTo(createDeliverableDTO.getIndex());
        assertThat(deliverableSaved.getProduct().getId()).isEqualTo(product.getId());
    }

    @Test
    void should_update_deliverable_by_id() {
        UpdateDeliverableDTO updateDeliverableDTO = new UpdateDeliverableDTO(
                1L, "title", 1, 0, ProgressionStatus.COMPLETED, 2L, updateCompletionDTO);

        when(deliverableRepository.findById(1L)).thenReturn(Optional.of(deliverable));
        when(deliverableRepository.save(deliverable)).thenReturn(deliverable);
        doReturn(completion).when(completionService).updateById(anyLong(), any());

        deliverableService.updateById(1L, updateDeliverableDTO);

        verify(deliverableRepository, times(1)).save(deliverableCaptor.capture());
        Deliverable deliverableSaved = deliverableCaptor.getValue();

        assertThat(deliverableSaved.getTitle()).isEqualTo(updateDeliverableDTO.getTitle());
        assertThat(deliverableSaved.getPosition()).isEqualTo(updateDeliverableDTO.getIndex());
        assertThat(deliverableSaved.getStatus()).isEqualTo(updateDeliverableDTO.getStatus());

    }

    @Test
    void should_bulk_update_deliverable() {
        UpdateDeliverableDTO updateDeliverableDTO = new UpdateDeliverableDTO(
                1L, "title", 1, 0, ProgressionStatus.COMPLETED, 2L, updateCompletionDTO);

        doReturn(deliverable).when(deliverableService).updateById(1L, updateDeliverableDTO);

        deliverableService.bulkUpdate(List.of(updateDeliverableDTO));
        verify(deliverableService, times(1)).updateById(1L, updateDeliverableDTO);

    }

    @Test
    void should_updateParentCompletion() {
        doReturn(deliverable).when(deliverableService).findByIdOrNull(any());
        doNothing().when(completionService).updateTarget(anyLong(), anyFloat());

        deliverableService.updateParentCompletion(1L, 1F);

        verify(completionService, times(1)).updateTarget(completion.getId(), 1F);
    }

    @Test
    void should_update_isArchived() {
        IsArchivedDTO isArchivedDTO = new IsArchivedDTO(true);

        when(deliverableRepository.findById(1L)).thenReturn(Optional.of(deliverable));
        when(deliverableRepository.save(deliverable)).thenReturn(deliverable);

        deliverableService.updateIsArchived(1L, isArchivedDTO);

        verify(deliverableRepository, times(1)).save(deliverableCaptor.capture());
        Deliverable deliverableSaved = deliverableCaptor.getValue();

        assertThat(deliverableSaved.getIsArchived()).isEqualTo(isArchivedDTO.getIsArchived());
    }

    @Test
    void should_delete_all_related_deliverables() {
        var deliverableToDelete = new Deliverable();
        deliverableToDelete.setId(2L);
        capability.setDeliverables(Set.of(deliverableToDelete));
        deliverableToDelete.setCapability(capability);
        deliverableToDelete.setPerformanceMeasure(performanceMeasure);
        deliverableToDelete.setChildren(Set.of(deliverable));

        doReturn(deliverableToDelete).when(deliverableService).findById(2L);

        deliverableService.deleteAllRelatedDeliverables(deliverableToDelete);
        verify(deliverableRepository, times(1)).deleteById(2L);
    }

    @Test
    void should_delete_deliverables_by_id() {
        var deliverableToDelete = new Deliverable();
        deliverableToDelete.setId(2L);
        deliverableToDelete.setCapability(capability);
        deliverableToDelete.setPerformanceMeasure(performanceMeasure);
        deliverableToDelete.setParent(deliverable);
        deliverableToDelete.setChildren(Set.of(deliverable));

        doReturn(deliverableToDelete).when(deliverableService).findById(2L);
        doNothing().when(deliverableService).updateParentCompletion(any(), anyFloat());

        deliverableService.deleteById(2L);
        verify(deliverableRepository, times(1)).deleteById(2L);
        verify(deliverableService, times(1)).updateParentCompletion(deliverable.getId(), -1F);
    }

}
