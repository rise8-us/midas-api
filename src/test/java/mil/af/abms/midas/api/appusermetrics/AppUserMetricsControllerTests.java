package mil.af.abms.midas.api.appusermetrics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.appusermetrics.dto.AppUserMetricsDTO;
import mil.af.abms.midas.api.dtos.metrics.UniqueRoleMetricsDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.JsonMapper;

@WebMvcTest({AppUserMetricsController.class})
public class AppUserMetricsControllerTests extends ControllerTestHarness {

    @Autowired
    AppUserMetricsService service;

    private final LocalDate DATE_ID1 = LocalDate.now().minusDays(1);
    private final LocalDate DATE_ID2 = LocalDate.now();
    private final AppUserMetrics appUserMetrics1 = Builder.build(AppUserMetrics.class)
            .with(a -> a.setId(DATE_ID1))
            .with(a -> a.setUniqueLogins(2L))
            .get();
    private final AppUserMetrics appUserMetrics2 = Builder.build(AppUserMetrics.class)
            .with(a -> a.setId(DATE_ID2))
            .with(a -> a.setUniqueLogins(3L))
            .get();
    private final UniqueRoleMetricsDTO uniqueRoleMetricsDTO = Builder.build(UniqueRoleMetricsDTO.class)
            .with(u -> u.setAdmins(Set.of()))
            .with(u -> u.setDesigners(Set.of()))
            .with(u -> u.setPlatformOperators(Set.of()))
            .with(u -> u.setPortfolioAdmins(Set.of()))
            .with(u -> u.setPortfolioLeads(Set.of()))
            .with(u -> u.setProductManagers(Set.of()))
            .with(u -> u.setStakeholders(Set.of()))
            .with(u -> u.setTechLeads(Set.of()))
            .with(u -> u.setUnassigned(Set.of()))
            .get();
    AppUserMetricsDTO dto = new AppUserMetricsDTO(DATE_ID1, 2L, uniqueRoleMetricsDTO);
    private final List<AppUserMetrics> appUserMetrics = List.of(appUserMetrics1, appUserMetrics2);

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_get_metrics_by_id() throws Exception {
        when(service.findById(appUserMetrics1.getId())).thenReturn(appUserMetrics1);

        mockMvc.perform(get("/api/appUserMetrics/" + DATE_ID1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.uniqueLogins").value(dto.getUniqueLogins()));
    }

    @Test
    void should_search_users() throws Exception {
        Page<AppUserMetrics> page = new PageImpl<>(appUserMetrics);
        List<AppUserMetricsDTO> appUserMetricsDTOs = appUserMetrics.stream().map(AppUserMetrics::toDto).collect(Collectors.toList());

        when(service.search(any(), any(), any(), any(), any())).thenReturn(page);
        when(service.preparePageResponse(any(), any())).thenReturn(appUserMetricsDTOs);

        mockMvc.perform(get("/api/appUserMetrics/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(JsonMapper.dateMapper().writeValueAsString(appUserMetricsDTOs))
                );
    }
}
