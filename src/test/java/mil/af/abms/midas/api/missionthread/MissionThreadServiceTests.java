package mil.af.abms.midas.api.missionthread;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.missionthread.dto.CreateMissionThreadDTO;
import mil.af.abms.midas.api.missionthread.dto.UpdateMissionThreadDTO;

@ExtendWith(SpringExtension.class)
@Import(MissionThreadService.class)
class MissionThreadServiceTests {

    @SpyBean
    MissionThreadService missionThreadService;
    @MockBean
    MissionThreadRepository missionThreadRepository;
    @Captor
    ArgumentCaptor<MissionThread> missionThreadCaptor;

    MissionThread missionThread = Builder.build(MissionThread.class)
            .with(m -> m.setId(1L))
            .with(m -> m.setTitle("title"))
            .with(m -> m.setIsArchived(false))
            .get();

    @Test
    void should_create_MissionThread() {
        CreateMissionThreadDTO createMissionThreadDTO = new CreateMissionThreadDTO("title");

        when(missionThreadRepository.save(missionThread)).thenReturn(new MissionThread());

        missionThreadService.create(createMissionThreadDTO);

        verify(missionThreadRepository, times(1)).save(missionThreadCaptor.capture());
        MissionThread MissionThreadSaved = missionThreadCaptor.getValue();

        assertThat(MissionThreadSaved.getTitle()).isEqualTo(createMissionThreadDTO.getTitle());
    }

    @Test
    void should_update_MissionThread_by_id() {
        UpdateMissionThreadDTO updateMissionThreadDTO = new UpdateMissionThreadDTO("title");

        when(missionThreadRepository.findById(1L)).thenReturn(Optional.of(missionThread));
        when(missionThreadRepository.save(missionThread)).thenReturn(missionThread);

        missionThreadService.updateById(1L, updateMissionThreadDTO);

        verify(missionThreadRepository, times(1)).save(missionThreadCaptor.capture());
        MissionThread MissionThreadSaved = missionThreadCaptor.getValue();

        assertThat(MissionThreadSaved.getTitle()).isEqualTo(updateMissionThreadDTO.getTitle());
    }

    @Test
    void should_update_isArchived() {
        IsArchivedDTO isArchivedDTO = new IsArchivedDTO(true);

        when(missionThreadRepository.findById(1L)).thenReturn(Optional.of(missionThread));
        when(missionThreadRepository.save(missionThread)).thenReturn(missionThread);

        missionThreadService.updateIsArchived(1L, isArchivedDTO);

        verify(missionThreadRepository, times(1)).save(missionThreadCaptor.capture());
        MissionThread missionThreadSaved = missionThreadCaptor.getValue();

        assertThat(missionThreadSaved.getIsArchived()).isEqualTo(isArchivedDTO.getIsArchived());
    }

}
