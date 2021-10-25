package mil.af.abms.midas.api.deliverable;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.capability.Capability;
import mil.af.abms.midas.api.capability.CapabilityService;
import mil.af.abms.midas.api.deliverable.dto.CreateDeliverableDTO;
import mil.af.abms.midas.api.deliverable.dto.UpdateDeliverableDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.performancemeasure.PerformanceMeasure;
import mil.af.abms.midas.api.performancemeasure.PerformanceMeasureService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.release.Release;
import mil.af.abms.midas.api.release.ReleaseService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.ProgressionStatus;

@ExtendWith(SpringExtension.class)
@Import(DeliverableService.class)
class DeliverableServiceTests {

    @SpyBean
    DeliverableService deliverableService;
    @MockBean
    ProductService productService;
    @MockBean
    ReleaseService releaseService;
    @MockBean
    UserService userService;
    @MockBean
    EpicService epicService;
    @MockBean
    CapabilityService capabilityService;
    @MockBean
    PerformanceMeasureService performanceMeasureService;
    @MockBean
    DeliverableRepository deliverableRepository;
    @Captor
    ArgumentCaptor<Deliverable> deliverableCaptor;

    private final User assignedTo = Builder.build(User.class).with(u -> u.setId(2L)).get();
    private final Release release = Builder.build(Release.class).with(p -> p.setId(3L)).get();
    private final Product product = Builder.build(Product.class).with(p -> p.setId(4L)).get();
    private final Capability capability = Builder.build(Capability.class).with(p -> p.setId(5L)).get();
    private final PerformanceMeasure performanceMeasure = Builder.build(PerformanceMeasure.class).with(p -> p.setId(5L)).get();
    private final Epic epic = Builder.build(Epic.class).with(e -> e.setId(6L)).get();
    private final Deliverable deliverable = Builder.build(Deliverable.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setTitle("title"))
            .with(d -> d.setProduct(product))
            .with(d -> d.setChildren(Set.of()))
            .with(d -> d.setStatus(ProgressionStatus.NOT_STARTED))
            .with(d -> d.setPosition(0))
            .with(d -> d.setReferenceId(1))
            .with(d -> d.setReleases(Set.of(release)))
            .with(d -> d.setPerformanceMeasure(performanceMeasure))
            .with(d -> d.setAssignedTo(assignedTo))
            .with(d -> d.setEpic(epic))
            .with(d -> d.setCapability(capability))
            .with(d -> d.setIsArchived(false))
            .get();

    @Test
    void should_create_deliverable() {
        CreateDeliverableDTO createDeliverableDTO = new CreateDeliverableDTO(
                "title", 1, 0, 4L, null, List.of(), List.of(10L), 9L, 5L, 2L, 6L);

        when(productService.findByIdOrNull(createDeliverableDTO.getProductId())).thenReturn(product);
        when(performanceMeasureService.findByIdOrNull(createDeliverableDTO.getPerformanceMeasureId())).thenReturn(performanceMeasure);
        when(releaseService.findById(release.getId())).thenReturn(release);
        when(deliverableRepository.save(deliverable)).thenReturn(new Deliverable());
        when(userService.findByIdOrNull(createDeliverableDTO.getAssignedToId())).thenReturn(assignedTo);
        when(capabilityService.findByIdOrNull(capability.getId())).thenReturn(capability);

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
                1L, "title", 1, 0, List.of(), ProgressionStatus.COMPLETED, 2L, 6L);

        when(deliverableRepository.findById(1L)).thenReturn(Optional.of(deliverable));
        when(deliverableRepository.save(deliverable)).thenReturn(deliverable);

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
                1L, "title", 1, 0, List.of(), ProgressionStatus.COMPLETED, 2L, 6L);

        doReturn(deliverable).when(deliverableService).updateById(1L, updateDeliverableDTO);

        deliverableService.bulkUpdate(List.of(updateDeliverableDTO));
        verify(deliverableService, times(1)).updateById(1L, updateDeliverableDTO);

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

}