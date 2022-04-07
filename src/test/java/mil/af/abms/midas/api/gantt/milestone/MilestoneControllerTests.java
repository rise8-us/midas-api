package mil.af.abms.midas.api.gantt.milestone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.gantt.milestone.dto.CreateMilestoneDTO;
import mil.af.abms.midas.api.gantt.milestone.dto.UpdateMilestoneDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@WebMvcTest({MilestoneController.class})
public class MilestoneControllerTests extends ControllerTestHarness {

    @MockBean
    private MilestoneService milestoneService;
    @MockBean
    private PortfolioService portfolioService;

    private final LocalDate DUE_DATE = TimeConversion.getLocalDateOrNullFromObject("2020-01-01");

    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("portfolio name"))
            .with(p -> p.setDescription("description"))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setGitlabGroupId(2))
            .with(p -> p.setPersonnel(new Personnel()))
            .with(p -> p.setProducts(Set.of()))
            .with(p -> p.setVision("vision"))
            .with(p -> p.setMission("mission"))
            .with(p -> p.setProblemStatement("problem"))
            .get();

    private final Milestone milestone = Builder.build(Milestone.class)
            .with(m -> m.setId(2L))
            .with(m -> m.setDueDate(DUE_DATE))
            .with(m -> m.setTitle("milestoneTitle"))
            .with(m -> m.setDescription("milestoneDescription"))
            .with(m -> m.setPortfolio(portfolio))
            .get();
    private final CreateMilestoneDTO createMilestoneDTO = Builder.build(CreateMilestoneDTO.class)
            .with(m -> m.setTitle("milestoneTitle"))
            .with(m -> m.setDescription("milestoneDescription"))
            .with(m -> m.setDueDate(DUE_DATE))
            .with(m -> m.setPortfolioId(milestone.getPortfolio().getId()))
            .get();

    private final UpdateMilestoneDTO updateMilestoneDTO = Builder.build(UpdateMilestoneDTO.class)
            .with(m -> m.setTitle("newTitle"))
            .with(m -> m.setDescription("newDescription"))
            .with(m -> m.setDueDate(DUE_DATE))
            .get();

    @BeforeEach
    void init() {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_milestone() throws Exception {
        doReturn(milestone).when(milestoneService).create(any(CreateMilestoneDTO.class));
        doReturn(true).when(portfolioService).existsById(anyLong());

        mockMvc.perform(post("/api/gantt_milestones")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createMilestoneDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("milestoneTitle"));
    }

    @Test
    void should_update_by_id() throws Exception {
        var newMilestone = new Milestone();
        BeanUtils.copyProperties(updateMilestoneDTO, newMilestone);
        newMilestone.setPortfolio(portfolio);

        when(milestoneService.updateById(any(), any(UpdateMilestoneDTO.class))).thenReturn(newMilestone);

        mockMvc.perform(put("/api/gantt_milestones/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(updateMilestoneDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("newTitle"));
    }

    @Test
    void should_throw_errors_due_to_null_parameters_on_create() throws Exception {
        var milestone2 = new CreateMilestoneDTO();
        BeanUtils.copyProperties(createMilestoneDTO, milestone2);
        milestone2.setTitle(null);
        milestone2.setPortfolioId(null);

        mockMvc.perform(post("/api/gantt_milestones")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(milestone2))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").exists())
                .andExpect(jsonPath("$.errors[2]").doesNotExist());
    }

    @Test
    void should_throw_errors_due_to_null_parameters_on_update() throws Exception {
        var milestone3 = new UpdateMilestoneDTO();
        BeanUtils.copyProperties(updateMilestoneDTO, milestone3);
        milestone3.setTitle(null);

        mockMvc.perform(put("/api/gantt_milestones/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(milestone3))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").doesNotExist());
    }

    @Test
    void should_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/gantt_milestones/1"))
                .andExpect(status().isOk());

        verify(milestoneService, times(1)).deleteById(1L);
    }
}
