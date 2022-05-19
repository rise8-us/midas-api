package mil.af.abms.midas.api.gantt.win;

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
import mil.af.abms.midas.api.gantt.win.dto.CreateWinDTO;
import mil.af.abms.midas.api.gantt.win.dto.UpdateWinDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@WebMvcTest({WinController.class})
public class WinControllerTests extends ControllerTestHarness {

    @MockBean
    private WinService winService;
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

    private final Win win = Builder.build(Win.class)
            .with(m -> m.setId(2L))
            .with(m -> m.setDueDate(DUE_DATE))
            .with(m -> m.setTitle("winTitle"))
            .with(m -> m.setDescription("winDescription"))
            .with(m -> m.setPortfolio(portfolio))
            .get();
    private final CreateWinDTO createWinDTO = Builder.build(CreateWinDTO.class)
            .with(m -> m.setTitle("winTitle"))
            .with(m -> m.setDescription("winDescription"))
            .with(m -> m.setDueDate(DUE_DATE))
            .with(m -> m.setPortfolioId(win.getPortfolio().getId()))
            .get();

    private final UpdateWinDTO updateWinDTO = Builder.build(UpdateWinDTO.class)
            .with(m -> m.setTitle("newTitle"))
            .with(m -> m.setDescription("newDescription"))
            .with(m -> m.setDueDate(DUE_DATE))
            .get();

    @BeforeEach
    void init() {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_win() throws Exception {
        doReturn(win).when(winService).create(any(CreateWinDTO.class));
        doReturn(true).when(portfolioService).existsById(anyLong());

        mockMvc.perform(post("/api/gantt_wins")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createWinDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("winTitle"));
    }

    @Test
    void should_update_by_id() throws Exception {
        var newWin = new Win();
        BeanUtils.copyProperties(updateWinDTO, newWin);
        newWin.setPortfolio(portfolio);

        when(winService.updateById(any(), any(UpdateWinDTO.class))).thenReturn(newWin);

        mockMvc.perform(put("/api/gantt_wins/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(updateWinDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("newTitle"));
    }

    @Test
    void should_throw_errors_due_to_null_parameters_on_create() throws Exception {
        var win2 = new CreateWinDTO();
        BeanUtils.copyProperties(createWinDTO, win2);
        win2.setTitle(null);
        win2.setPortfolioId(null);

        mockMvc.perform(post("/api/gantt_wins")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(win2))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").exists())
                .andExpect(jsonPath("$.errors[2]").doesNotExist());
    }

    @Test
    void should_throw_errors_due_to_null_parameters_on_update() throws Exception {
        var win3 = new UpdateWinDTO();
        BeanUtils.copyProperties(updateWinDTO, win3);
        win3.setTitle(null);

        mockMvc.perform(put("/api/gantt_wins/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(win3))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").doesNotExist());
    }

    @Test
    void should_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/gantt_wins/1"))
                .andExpect(status().isOk());

        verify(winService, times(1)).deleteById(1L);
    }
}
