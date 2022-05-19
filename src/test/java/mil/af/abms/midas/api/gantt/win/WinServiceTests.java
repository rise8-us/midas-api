package mil.af.abms.midas.api.gantt.win;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.gantt.win.dto.CreateWinDTO;
import mil.af.abms.midas.api.gantt.win.dto.UpdateWinDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@ExtendWith(SpringExtension.class)
@Import(WinService.class)
public class WinServiceTests {

    @SpyBean
    private WinService winService;
    @MockBean
    private WinRepository repository;
    @MockBean
    private PortfolioService portfolioService;

    @Captor
    private ArgumentCaptor<Win> winArgumentCaptor;
    @Captor
    private ArgumentCaptor<Portfolio> portfolioArgumentCaptor;
    @Captor
    private  ArgumentCaptor<Long> longArgumentCaptor;

    private LocalDate DUE_DATE = TimeConversion.getLocalDateOrNullFromObject("2021-07-09");
    private LocalDate DUE_DATE2 = TimeConversion.getLocalDateOrNullFromObject("2022-07-09");
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
            .with(m -> m.setDueDate(DUE_DATE2))
            .get();

    @Test
    void should_create_win() {
        winService.create(createWinDTO);

        verify(repository, times(1)).save(any());
        verify(winService, times(1)).updateCommonFields(any(), any());
    }

    @Test
    void should_update_win_by_id() {
        doReturn(win).when(winService).findById(2L);

        winService.updateById(2L, updateWinDTO);

        verify(repository, times(1)).save(winArgumentCaptor.capture());
        Win winSaved = winArgumentCaptor.getValue();

        assertThat(winSaved.getTitle()).isEqualTo("newTitle");
        assertThat(winSaved.getDescription()).isEqualTo("newDescription");
    }

    @Test
    void should_set_due_date() {
        doReturn(win).when(winService).findById(anyLong());

        winService.updateById(win.getId(), updateWinDTO);

        verify(repository, times(1)).save(winArgumentCaptor.capture());
        Win winSaved = winArgumentCaptor.getValue();

        AssertionsForClassTypes.assertThat(winSaved.getDueDate()).isEqualTo(DUE_DATE2);
    }


}
