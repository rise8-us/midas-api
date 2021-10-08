package mil.af.abms.midas.api.release;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import mil.af.abms.midas.api.deliverable.DeliverableService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.release.dto.CreateReleaseDTO;
import mil.af.abms.midas.api.release.dto.UpdateReleaseDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@ExtendWith(SpringExtension.class)
@Import(ReleaseService.class)
public class ReleaseServiceTests {

    @SpyBean
    ReleaseService releaseService;
    @MockBean
    DeliverableService deliverableService;
    @MockBean
    ReleaseRepository releaseRepository;
    @Captor
    ArgumentCaptor<Release> releaseCaptor;

    Release release = Builder.build(Release.class)
            .with(r -> r.setId(1L))
            .with(r -> r.setTitle("release"))
            .with(r -> r.setStatus(ProgressionStatus.NOT_STARTED))
            .with(r -> r.setDeliverables(Set.of()))
            .with(r -> r.setTargetDate(LocalDateTime.of(2021, 10, 10, 0, 0)))
            .with(r -> r.setIsArchived(false))
            .get();

    @Test
    void should_create_release() {
        CreateReleaseDTO createReleaseDTO = new CreateReleaseDTO(
                "release", "2021-10-10"
        );

        when(releaseRepository.save(any())).thenReturn(release);

        releaseService.create(createReleaseDTO);

        verify(releaseRepository, times(1)).save(releaseCaptor.capture());
        Release releaseSaved = releaseCaptor.getValue();

        assertThat(releaseSaved.getTitle()).isEqualTo(createReleaseDTO.getTitle());
        assertThat(releaseSaved.getTargetDate()).isEqualTo(LocalDateTime.of(2021, 10, 10, 0, 0));
    }

    @Test
    void should_update_release_by_id() {
        UpdateReleaseDTO updateReleaseDTO = new UpdateReleaseDTO(
                "update release", "2021-10-10", ProgressionStatus.COMPLETED, Set.of()
        );

        when(releaseRepository.findById(1L)).thenReturn(Optional.of(release));
        when(releaseRepository.save(release)).thenReturn(release);

        releaseService.updateById(1L, updateReleaseDTO);

        verify(releaseRepository, times(1)).save(releaseCaptor.capture());
        Release releaseSaved = releaseCaptor.getValue();

        assertThat(releaseSaved.getTitle()).isEqualTo(updateReleaseDTO.getTitle());
        assertThat(releaseSaved.getStatus()).isEqualTo(updateReleaseDTO.getStatus());
        assertThat(releaseSaved.getTargetDate()).isEqualTo(LocalDateTime.of(2021, 10, 10, 0, 0));
        assertThat(releaseSaved.getDeliverables()).isEqualTo(Set.of());
    }

    @Test
    void should_update_isArchived() {
        IsArchivedDTO isArchivedDTO = new IsArchivedDTO(true);

        when(releaseRepository.findById(1L)).thenReturn(Optional.of(release));
        when(releaseRepository.save(release)).thenReturn(release);

        releaseService.updateIsArchived(1L, isArchivedDTO);

        verify(releaseRepository, times(1)).save(releaseCaptor.capture());
        Release releaseSaved = releaseCaptor.getValue();

        assertThat(releaseSaved.getIsArchived()).isEqualTo(isArchivedDTO.getIsArchived());
    }
}
