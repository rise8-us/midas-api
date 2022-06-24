package mil.af.abms.midas.api.announcement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.announcement.dto.CreateAnnouncementDTO;
import mil.af.abms.midas.api.announcement.dto.UpdateAnnouncementDTO;
import mil.af.abms.midas.api.helper.Builder;

@WebMvcTest({AnnouncementController.class})
class AnnouncementControllerTests extends ControllerTestHarness {
    
    @MockBean
    private AnnouncementService announcementService;

    private final Announcement announcement = Builder.build(Announcement.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setCreationDate(LocalDateTime.now()))
            .with(a -> a.setMessage("Test Me")).get();
    
    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_announcement() throws Exception {
        CreateAnnouncementDTO createAnnouncementDTO = Builder.build(CreateAnnouncementDTO.class)
                .with(c -> c.setMessage("Test Me")).get();

        when(announcementService.create(createAnnouncementDTO)).thenReturn(announcement);

        mockMvc.perform(post("/api/announcements")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createAnnouncementDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(createAnnouncementDTO.getMessage()));
    }

    @Test
    void should_throw_error_when_message_is_empty_on_create_announcement() throws Exception {
        mockMvc.perform(post("/api/announcements")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(new CreateAnnouncementDTO()))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value("announcement message must not be blank"));
    }

    @Test
    void should_update_announcement_by_id() throws Exception {
        UpdateAnnouncementDTO updateAnnouncementDTO = Builder.build(UpdateAnnouncementDTO.class)
                .with(d -> d.setMessage("Diff test")).get();
        Announcement expectedAnnouncement = new Announcement();
        BeanUtils.copyProperties(announcement, expectedAnnouncement);
        expectedAnnouncement.setMessage(updateAnnouncementDTO.getMessage());

        when(announcementService.update(any(), any())).thenReturn(expectedAnnouncement);

        mockMvc.perform(put("/api/announcements/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateAnnouncementDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(updateAnnouncementDTO.getMessage()));
    }

    @Test
    void should_throw_error_when_message_is_empty_on_update_announcement() throws Exception {
        mockMvc.perform(put("/api/announcements/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(new UpdateAnnouncementDTO()))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value("announcement message must not be blank"));
    }
}
