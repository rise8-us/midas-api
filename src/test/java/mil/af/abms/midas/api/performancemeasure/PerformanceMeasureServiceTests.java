package mil.af.abms.midas.api.performancemeasure;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

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
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.performancemeasure.dto.CreatePerformanceMeasureDTO;
import mil.af.abms.midas.api.performancemeasure.dto.UpdatePerformanceMeasureDTO;

@ExtendWith(SpringExtension.class)
@Import(PerformanceMeasureService.class)
class PerformanceMeasureServiceTests {

    @SpyBean
    PerformanceMeasureService performanceMeasureService;
    @MockBean
    CapabilityService capabilityService;
    @MockBean
    PerformanceMeasureRepository performanceMeasureRepository;
    @Captor
    ArgumentCaptor<PerformanceMeasure> performanceMeasureCaptor;

    Capability capability = Builder.build(Capability.class).with(c -> c.setId(2L)).get();

    PerformanceMeasure performanceMeasure = Builder.build(PerformanceMeasure.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setTitle("title"))
            .with(p -> p.setReferenceId(0))
            .with(p -> p.setCapability(capability))
            .with(p -> p.setIsArchived(false))
            .get();

    @Test
    void should_create_performanceMeasure() {
        CreatePerformanceMeasureDTO createPerformanceMeasureDTO = new CreatePerformanceMeasureDTO("title", 0, 2L);

        when(performanceMeasureRepository.save(performanceMeasure)).thenReturn(new PerformanceMeasure());
        when(capabilityService.findById(capability.getId())).thenReturn(capability);

        performanceMeasureService.create(createPerformanceMeasureDTO);

        verify(performanceMeasureRepository, times(1)).save(performanceMeasureCaptor.capture());
        PerformanceMeasure performanceMeasureSaved = performanceMeasureCaptor.getValue();

        assertThat(performanceMeasureSaved.getTitle()).isEqualTo(createPerformanceMeasureDTO.getTitle());
        assertThat(performanceMeasureSaved.getReferenceId()).isEqualTo(createPerformanceMeasureDTO.getReferenceId());
    }

    @Test
    void should_update_performanceMeasure_by_id() {
        UpdatePerformanceMeasureDTO updatePerformanceMeasureDTO = new UpdatePerformanceMeasureDTO(1L, "title", 0);

        when(performanceMeasureRepository.findById(1L)).thenReturn(Optional.of(performanceMeasure));
        when(performanceMeasureRepository.save(performanceMeasure)).thenReturn(performanceMeasure);

        performanceMeasureService.updateById(1L, updatePerformanceMeasureDTO);

        verify(performanceMeasureRepository, times(1)).save(performanceMeasureCaptor.capture());
        PerformanceMeasure performanceMeasureSaved = performanceMeasureCaptor.getValue();

        assertThat(performanceMeasureSaved.getTitle()).isEqualTo(updatePerformanceMeasureDTO.getTitle());
        assertThat(performanceMeasureSaved.getReferenceId()).isEqualTo(updatePerformanceMeasureDTO.getReferenceId());
    }

    @Test
    void should_bulk_update_performanceMeasure() {
        UpdatePerformanceMeasureDTO updatePerformanceMeasureDTO = new UpdatePerformanceMeasureDTO(
                1L, "test_pm", 2
        );

        doReturn(performanceMeasure).when(performanceMeasureService).updateById(1L, updatePerformanceMeasureDTO);

        performanceMeasureService.bulkUpdate(List.of(updatePerformanceMeasureDTO));
        verify(performanceMeasureService, times(1)).updateById(1L, updatePerformanceMeasureDTO);

    }

    @Test
    void should_update_isArchived() {
        IsArchivedDTO isArchivedDTO = new IsArchivedDTO(true);

        when(performanceMeasureRepository.findById(1L)).thenReturn(Optional.of(performanceMeasure));
        when(performanceMeasureRepository.save(performanceMeasure)).thenReturn(performanceMeasure);

        performanceMeasureService.updateIsArchived(1L, isArchivedDTO);

        verify(performanceMeasureRepository, times(1)).save(performanceMeasureCaptor.capture());
        PerformanceMeasure performanceMeasureSaved = performanceMeasureCaptor.getValue();

        assertThat(performanceMeasureSaved.getIsArchived()).isEqualTo(isArchivedDTO.getIsArchived());
    }

}
