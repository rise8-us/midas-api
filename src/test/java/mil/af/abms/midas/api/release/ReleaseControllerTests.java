package mil.af.abms.midas.api.release;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.release.dto.CreateReleaseDTO;
import mil.af.abms.midas.api.release.dto.UpdateReleaseDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@WebMvcTest({ReleaseController.class})
public class ReleaseControllerTests extends ControllerTestHarness {

    @MockBean
    private ReleaseService releaseService;

    private final CreateReleaseDTO createReleaseDTO = new CreateReleaseDTO(
            "title", "2021-11-10"
    );
    private final UpdateReleaseDTO updateReleaseDTO = new UpdateReleaseDTO(
            "new title", "11-10-2021", ProgressionStatus.COMPLETED, Set.of()
    );

    private final Release createRelease = Builder.build(Release.class)
            .with(r -> r.setTitle(createReleaseDTO.getTitle()))
            .with(r -> r.setIsArchived(false))
            .get();

    private final Release updateRelease = Builder.build(Release.class)
            .with(r -> r.setId(2L))
            .with(r -> r.setTitle(updateReleaseDTO.getTitle()))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_release() throws Exception {
        when(releaseService.create(any(CreateReleaseDTO.class))).thenReturn(createRelease);

        mockMvc.perform(post("/api/releases")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createReleaseDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(createRelease.getTitle()));
    }

    @Test
    public void should_update_release() throws Exception {
        when(releaseService.updateById(anyLong(), any(UpdateReleaseDTO.class))).thenReturn(updateRelease);

        mockMvc.perform(put("/api/releases/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateReleaseDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(updateRelease.getTitle()));

    }

    @Test
    public void should_update_release_is_archived_true() throws Exception {
        Release archived = new Release();
        BeanUtils.copyProperties(createRelease, archived);
        archived.setIsArchived(true);

        IsArchivedDTO archiveDTO = new IsArchivedDTO(true);

        when(releaseService.updateIsArchived(1L, archiveDTO)).thenReturn(archived);

        mockMvc.perform(put("/api/releases/1/archive")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(archiveDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(true));
    }

}
