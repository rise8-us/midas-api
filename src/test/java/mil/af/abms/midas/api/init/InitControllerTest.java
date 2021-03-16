package mil.af.abms.midas.api.init;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.announcement.Announcement;
import mil.af.abms.midas.api.announcement.AnnouncementService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.config.CustomProperty;

@WebMvcTest(InitController.class)
public class InitControllerTest extends ControllerTestHarness {

    @MockBean
    private CustomProperty property;
    @MockBean
    private AnnouncementService announcementService;

    @BeforeEach
    public void init() {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
        when(userService.getUserFromAuth(any())).thenReturn(authUser);
    }

    @Test
    public void should_get_from_public_info() throws Exception {
        Announcement announcementEntity = Builder.build(Announcement.class)
                .with(a -> a.setId(1L))
                .with(a -> a.setCreationDate(LocalDateTime.now()))
                .with(a -> a.setMessage("HELLO")).get();

        when(userService.getUserFromAuth(any())).thenReturn(authUser);
        when(announcementService.getUnseenAnnouncements(any())).thenReturn(List.of(announcementEntity));
        when(property.getClassification()).thenReturn("UNCLASS");
        when(property.getCaveat()).thenReturn("IL2");

        mockMvc.perform(get("/init"))
                .andExpect(jsonPath("$.classification").isMap())
                .andExpect(jsonPath("$.classification.name").isNotEmpty())
                .andExpect(jsonPath("$.classification.caveat").isNotEmpty())
                .andExpect(jsonPath("$.classification.backgroundColor").isNotEmpty())
                .andExpect(jsonPath("$.classification.textColor").isNotEmpty())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[*].name").isNotEmpty())
                .andExpect(jsonPath("$.roles[*].offset").isNotEmpty())
                .andExpect(jsonPath("$.roles[*].description").isNotEmpty())
                .andExpect(jsonPath("$.userDTO").isNotEmpty())
                .andExpect(jsonPath("$.userDTO.keycloakUid").value("abc-123"))
                .andExpect(jsonPath("$.announcementDTOs[0].message").value("HELLO"))
                .andExpect(jsonPath("$.announcementDTOs").isArray());
    }
}
