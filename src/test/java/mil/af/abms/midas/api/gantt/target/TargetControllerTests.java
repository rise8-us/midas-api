package mil.af.abms.midas.api.gantt.target;

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

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.gantt.target.dto.CreateTargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.UpdateTargetDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@WebMvcTest({TargetController.class})
public class TargetControllerTests extends ControllerTestHarness {

    @MockBean
    private TargetService targetService;
    @MockBean
    private PortfolioService portfolioService;

    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .get();
    private final Target target = Builder.build(Target.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setStartDate(LocalDate.now()))
            .with(t -> t.setDueDate(LocalDate.now().plusDays(1)))
            .with(t -> t.setTitle("This is the title"))
            .with(t -> t.setDescription("This is the description"))
            .with(t -> t.setPortfolio(portfolio))
            .get();
    private final CreateTargetDTO createTargetDTO = Builder.build(CreateTargetDTO.class)
            .with(t -> t.setStartDate(target.getStartDate()))
            .with(t -> t.setDueDate(target.getDueDate()))
            .with(t -> t.setTitle(target.getTitle()))
            .with(t -> t.setDescription(target.getDescription()))
            .with(t -> t.setPortfolioId(target.getPortfolio().getId()))
            .get();
    private final UpdateTargetDTO updateTargetDTO = Builder.build(UpdateTargetDTO.class)
            .with(t -> t.setStartDate(target.getStartDate()))
            .with(t -> t.setDueDate(target.getDueDate()))
            .with(t -> t.setTitle("This is an updated title"))
            .with(t -> t.setDescription("This is an updated description"))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_target() throws Exception {
        doReturn(target).when(targetService).create(any(CreateTargetDTO.class));
        doReturn(true).when(portfolioService).existsById(anyLong());

        mockMvc.perform(post("/api/gantt_targets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createTargetDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("This is the title"));
    }

    @Test
    void should_update_by_id() throws Exception {
        var newTarget = new Target();
        BeanUtils.copyProperties(updateTargetDTO, newTarget);
        when(targetService.updateById(any(), any(UpdateTargetDTO.class))).thenReturn(newTarget);

        mockMvc.perform(put("/api/gantt_targets/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateTargetDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("This is an updated title"))
                .andExpect(jsonPath("$.description").value("This is an updated description"));
    }

    @Test
    void should_throw_errors_due_to_null_parameters_on_create() throws Exception {
        var target2 = new CreateTargetDTO();
        BeanUtils.copyProperties(createTargetDTO, target2);
        target2.setTitle(null);
        target2.setPortfolioId(null);

        mockMvc.perform(post("/api/gantt_targets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(target2))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").exists())
                .andExpect(jsonPath("$.errors[2]").doesNotExist());
    }

    @Test
    void should_throw_errors_due_to_null_parameters_on_update() throws Exception {
        var target3 = new UpdateTargetDTO();
        BeanUtils.copyProperties(updateTargetDTO, target3);
        target3.setTitle(null);

        mockMvc.perform(put("/api/gantt_targets/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(target3))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").doesNotExist());
    }

    @Test
    void should_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/gantt_targets/1"))
                .andExpect(status().isOk());

        verify(targetService, times(1)).deleteById(1L);
    }

}
