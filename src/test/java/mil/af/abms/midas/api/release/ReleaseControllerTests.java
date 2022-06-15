package mil.af.abms.midas.api.release;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;

@WebMvcTest({ReleaseController.class})
public class ReleaseControllerTests extends ControllerTestHarness {

    @MockBean
    private ReleaseService releaseService;

    SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(sc -> sc.setBaseUrl("fake_url"))
            .with(sc -> sc.setToken("fake_token"))
            .get();

    Project project = Builder.build(Project.class)
            .with(p -> p.setName("name"))
            .with(p -> p.setId(2L))
            .with(p -> p.setSourceControl(sourceControl))
            .get();

    Release release = Builder.build(Release.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setCreationDate(LocalDateTime.now()))
            .with(e -> e.setProject(project))
            .with(e -> e.setName("name"))
            .get();

    @BeforeEach
    void init() {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_sync_all_releases_by_project_id() throws Exception {
        when(releaseService.syncGitlabReleaseForProject(any())).thenReturn(Set.of(release));

        mockMvc.perform(get("/api/releases/sync/project/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void should_get_all_releases_by_project_id() throws Exception {
        when(releaseService.getAllReleasesByProjectId(any())).thenReturn(List.of(release));

        mockMvc.perform(get("/api/releases/project/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void should_get_all_releases_by_product_id() throws Exception {
        when(releaseService.getAllReleasesByProductId(any())).thenReturn(List.of(release));

        mockMvc.perform(get("/api/releases/product/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void should_sync_all_releases_by_product_id() throws Exception {
        when(releaseService.syncGitlabReleaseForProduct(any())).thenReturn(Set.of(release));

        mockMvc.perform(get("/api/releases/sync/product/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

}
