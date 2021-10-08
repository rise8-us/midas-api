package mil.af.abms.midas.api.capability;

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

import mil.af.abms.midas.api.capability.dto.CreateCapabilityDTO;
import mil.af.abms.midas.api.capability.dto.UpdateCapabilityDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.missionthread.MissionThreadService;

@ExtendWith(SpringExtension.class)
@Import(CapabilityService.class)
public class CapabilityServiceTests {

    @SpyBean
    CapabilityService capabilityService;
    @MockBean
    MissionThreadService missionThreadService;
    @MockBean
    CapabilityRepository capabilityRepository;
    @Captor
    ArgumentCaptor<Capability> capabilityCaptor;

    Capability capability = Builder.build(Capability.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setTitle("title"))
            .with(c -> c.setDescription("description"))
            .with(c -> c.setReferenceId(0))
            .with(c -> c.setIsArchived(false))
            .get();

    @Test
    void should_create_capability() {
        CreateCapabilityDTO createCapabilityDTO = new CreateCapabilityDTO("title", "description", 0);

        when(capabilityRepository.save(capability)).thenReturn(new Capability());

        capabilityService.create(createCapabilityDTO);

        verify(capabilityRepository, times(1)).save(capabilityCaptor.capture());
        Capability capabilitySaved = capabilityCaptor.getValue();

        assertThat(capabilitySaved.getTitle()).isEqualTo(createCapabilityDTO.getTitle());
        assertThat(capabilitySaved.getReferenceId()).isEqualTo(createCapabilityDTO.getReferenceId());
    }

    @Test
    void should_update_capability_by_id() {
        UpdateCapabilityDTO updateCapabilityDTO = new UpdateCapabilityDTO("title", "description", 0);

        when(capabilityRepository.findById(1L)).thenReturn(Optional.of(capability));
        when(capabilityRepository.save(capability)).thenReturn(capability);

        capabilityService.updateById(1L, updateCapabilityDTO);

        verify(capabilityRepository, times(1)).save(capabilityCaptor.capture());
        Capability capabilitySaved = capabilityCaptor.getValue();

        assertThat(capabilitySaved.getTitle()).isEqualTo(updateCapabilityDTO.getTitle());
        assertThat(capabilitySaved.getReferenceId()).isEqualTo(updateCapabilityDTO.getReferenceId());
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

}
