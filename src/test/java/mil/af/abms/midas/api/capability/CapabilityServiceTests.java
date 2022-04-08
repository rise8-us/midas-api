package mil.af.abms.midas.api.capability;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import mil.af.abms.midas.api.capability.dto.CreateCapabilityDTO;
import mil.af.abms.midas.api.capability.dto.UpdateCapabilityDTO;
import mil.af.abms.midas.api.deliverable.Deliverable;
import mil.af.abms.midas.api.deliverable.DeliverableService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.missionthread.MissionThreadService;
import mil.af.abms.midas.api.performancemeasure.PerformanceMeasureService;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@ExtendWith(SpringExtension.class)
@Import(CapabilityService.class)
public class CapabilityServiceTests {

    @SpyBean
    CapabilityService capabilityService;
    @MockBean
    DeliverableService deliverableService;
    @MockBean
    PerformanceMeasureService performanceMeasureService;
    @MockBean
    MissionThreadService missionThreadService;
    @MockBean
    PortfolioService portfolioService;
    @MockBean
    CapabilityRepository capabilityRepository;
    @Captor
    ArgumentCaptor<Capability> capabilityCaptor;
    @MockBean
    SimpMessageSendingOperations websocket;

    private final Capability capability = Builder.build(Capability.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setTitle("title"))
            .with(c -> c.setDescription("description"))
            .with(c -> c.setReferenceId(0))
            .with(c -> c.setIsArchived(false))
            .get();

    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(5L))
            .with(p -> p.setCapabilities(Set.of(capability)))
            .get();

    private final Deliverable deliverable = Builder.build(Deliverable.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setTitle("title"))
            .with(d -> d.setCapability(capability))
            .get();

    @Test
    void should_create_capability() {
        CreateCapabilityDTO createCapabilityDTO = Builder.build(CreateCapabilityDTO.class)
                .with(d -> d.setTitle("title"))
                .with(d -> d.setDescription("description"))
                .with(d -> d.setReferenceId(0))
                .with(d -> d.setPortfolioId(5L))
                .get();

        when(capabilityRepository.save(capability)).thenReturn(new Capability());
        when(portfolioService.findByIdOrNull(anyLong())).thenReturn(portfolio);

        capabilityService.create(createCapabilityDTO);

        verify(capabilityRepository, times(1)).save(capabilityCaptor.capture());
        Capability capabilitySaved = capabilityCaptor.getValue();

        assertThat(capabilitySaved.getTitle()).isEqualTo(createCapabilityDTO.getTitle());
        assertThat(capabilitySaved.getReferenceId()).isEqualTo(createCapabilityDTO.getReferenceId());
        assertThat(capabilitySaved.getPortfolio()).isEqualTo(portfolio);
    }

    @Test
    void should_update_capability_by_id() {
        UpdateCapabilityDTO updateCapabilityDTO = Builder.build(UpdateCapabilityDTO.class)
                .with(d -> d.setTitle("title"))
                .with(d -> d.setDescription("description"))
                .with(d -> d.setReferenceId(0))
                .with(d -> d.setPortfolioId(5L))
                .get();

        when(capabilityRepository.findById(1L)).thenReturn(Optional.of(capability));
        when(capabilityRepository.save(capability)).thenReturn(capability);
        when(portfolioService.findByIdOrNull(anyLong())).thenReturn(portfolio);

        capabilityService.updateById(1L, updateCapabilityDTO);

        verify(capabilityRepository, times(1)).save(capabilityCaptor.capture());
        Capability capabilitySaved = capabilityCaptor.getValue();

        assertThat(capabilitySaved.getTitle()).isEqualTo(updateCapabilityDTO.getTitle());
        assertThat(capabilitySaved.getReferenceId()).isEqualTo(updateCapabilityDTO.getReferenceId());
        assertThat(capabilitySaved.getPortfolio()).isEqualTo(portfolio);
    }

    @Test
    void should_update_isArchived() {
        IsArchivedDTO isArchivedDTO = new IsArchivedDTO(true);

        when(capabilityRepository.findById(1L)).thenReturn(Optional.of(capability));
        when(capabilityRepository.save(capability)).thenReturn(capability);

        capabilityService.updateIsArchived(1L, isArchivedDTO);

        verify(capabilityRepository, times(1)).save(capabilityCaptor.capture());
        Capability capabilitySaved = capabilityCaptor.getValue();

        assertThat(capabilitySaved.getIsArchived()).isEqualTo(isArchivedDTO.getIsArchived());
    }

    @Test
    void should_deleteById() {
        var capabilityToDelete = new Capability();
        capabilityToDelete.setId(1L);
        capabilityToDelete.setDeliverables(Set.of(deliverable));

        doReturn(capabilityToDelete).when(capabilityService).findById(1L);

        capabilityService.deleteById((1L));
        verify(capabilityRepository, times(1)).deleteById(1L);
    }

}
