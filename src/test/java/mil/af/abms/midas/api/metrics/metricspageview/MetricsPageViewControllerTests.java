package mil.af.abms.midas.api.metrics.metricspageview;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.metrics.dtos.MetricsPageViewDTO;
import mil.af.abms.midas.api.metrics.dtos.UpdateMetricsPageViewDTO;
import mil.af.abms.midas.api.metrics.metricspageview.dto.CreateOrUpdatePageViewsDTO;

@WebMvcTest({MetricsPageViewController.class})
public class MetricsPageViewControllerTests extends ControllerTestHarness {

    @MockBean
    private MetricsPageViewService service;

    private final LocalDate DATE_ID = LocalDate.now();
    private final Map<String, Set<Long>> pageViewsTest = new HashMap<>(
            Map.of("foo/bar", Set.of(1L))
    );
    private final MetricsPageView metricsPageView = Builder.build(MetricsPageView.class)
            .with(a -> a.setId(DATE_ID))
            .with(a -> a.setPageViews(pageViewsTest))
            .get();
    private final MetricsPageViewDTO dto = Builder.build(MetricsPageViewDTO.class)
            .with(a -> a.setId(DATE_ID))
            .with(a -> a.setPageViews(pageViewsTest))
            .get();
    private final UpdateMetricsPageViewDTO updateDTO = Builder.build(UpdateMetricsPageViewDTO.class)
            .with(u -> u.setPathname("foo/bar"))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_post_createOrUpdate() throws Exception {
        when(service.determineCreateOrUpdate(any(CreateOrUpdatePageViewsDTO.class))).thenReturn(metricsPageView);

        mockMvc.perform(post("/api/metrics_page_view")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(dto.getId().toString()))
                .andExpect(jsonPath("$.pageViews.length()").value(1));
    }

}
