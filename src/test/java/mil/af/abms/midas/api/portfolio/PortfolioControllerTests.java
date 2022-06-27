package mil.af.abms.midas.api.portfolio;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.capability.CapabilityService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.dtos.SprintProductMetricsDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.PersonnelService;
import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({PortfolioController.class})
class PortfolioControllerTests extends ControllerTestHarness {

    @MockBean
    PortfolioService portfolioService;
    @MockBean
    ProductService productService;
    @MockBean
    PersonnelService personnelService;
    @MockBean
    SourceControlService sourceControlService;
    @MockBean
    CapabilityService capabilityService;

    private final UpdatePortfolioDTO updatePortfolioDTO = Builder.build(UpdatePortfolioDTO.class)
            .with(d -> d.setName("new name"))
            .get();
    private final CreatePortfolioDTO createPortfolioDTO = Builder.build(CreatePortfolioDTO.class)
            .with(d -> d.setName("name"))
            .with(d -> d.setDescription("description"))
            .get();
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("name"))
            .with(p -> p.setDescription("description"))
            .with(p -> p.setIsArchived(false))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_throw_unique_name_validation_on_create() throws Exception {
        String errorMessage = "portfolio name already exists";

        when(portfolioService.findByName(createPortfolioDTO.getName())).thenReturn(portfolio);
        when(portfolioService.create(any(CreatePortfolioDTO.class))).thenReturn(portfolio);
        when(sourceControlService.findByIdOrNull(any())).thenReturn(null);
        when(personnelService.create(any(CreatePersonnelDTO.class))).thenReturn(new Personnel());

        mockMvc.perform(post("/api/portfolios")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createPortfolioDTO))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value(errorMessage));
    }

    @Test
    void should_create() throws Exception {
        when(portfolioService.findByName(createPortfolioDTO.getName())).thenThrow(EntityNotFoundException.class);
        when(portfolioService.create(any(CreatePortfolioDTO.class))).thenReturn(portfolio);
        when(sourceControlService.findByIdOrNull(any())).thenReturn(null);
        when(personnelService.create(any(CreatePersonnelDTO.class))).thenReturn(new Personnel());

        mockMvc.perform(post("/api/portfolios")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createPortfolioDTO))
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void should_updateById() throws Exception {
        when(portfolioService.findByName(updatePortfolioDTO.getName())).thenReturn(portfolio);
        when(portfolioService.findById(anyLong())).thenReturn(portfolio);
        when(portfolioService.updateById(anyLong(), any(UpdatePortfolioDTO.class))).thenReturn(portfolio);
        when(sourceControlService.findByIdOrNull(any())).thenReturn(null);
        when(personnelService.create(any(CreatePersonnelDTO.class))).thenReturn(new Personnel());

        mockMvc.perform(put("/api/portfolios/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updatePortfolioDTO))
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void should_updateIsArchivedById() throws Exception {
        IsArchivedDTO isArchivedDTO = Builder.build(IsArchivedDTO.class)
                .with(d -> d.setIsArchived(true))
                .get();
        portfolio.setIsArchived(true);

        when(portfolioService.updateIsArchivedById(anyLong(), any(IsArchivedDTO.class))).thenReturn(portfolio);
        when(sourceControlService.findByIdOrNull(any())).thenReturn(null);
        when(personnelService.create(any(CreatePersonnelDTO.class))).thenReturn(new Personnel());

        mockMvc.perform(put("/api/portfolios/1/archive")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(isArchivedDTO))
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.isArchived").value(true));
    }

    @Test
    void should_get_sprint_metrics() throws Exception {
        SprintProductMetricsDTO dto = Builder.build(SprintProductMetricsDTO.class)
                .with(d -> d.setDate(LocalDate.parse("2022-06-16")))
                .with(d -> d.setDeliveredPoints(100L))
                .with(d -> d.setDeliveredStories(60))
                .get();

        HashMap<Long, List<SprintProductMetricsDTO>> metricsMap = new HashMap<>();
        metricsMap.put(1L, List.of(dto));

        when(portfolioService.getSprintMetrics(any(), any(), any(), any())).thenReturn(metricsMap);

        mockMvc.perform(get("/api/portfolios/1/sprint-metrics/2022-06-16?duration=14&sprints=1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(metricsMap))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$['1'][0]['date']").value("2022-06-16"));
    }
}
