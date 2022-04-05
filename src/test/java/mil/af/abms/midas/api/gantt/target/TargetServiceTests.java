package mil.af.abms.midas.api.gantt.target;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.gantt.target.dto.CreateTargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.UpdateTargetDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.user.UserService;

@ExtendWith(SpringExtension.class)
@Import(TargetService.class)
public class TargetServiceTests {

    @SpyBean
    TargetService targetService;
    @MockBean
    SimpMessageSendingOperations websocket;
    @MockBean
    UserService userService;
    @MockBean
    PortfolioService portfolioService;
    @MockBean
    TargetRepository targetRepository;
    @Captor
    ArgumentCaptor<Target> targetCaptor;

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

    @Test
    void should_create_target() {
        doReturn(portfolio).when(portfolioService).findById(anyLong());
        when(portfolioService.findById(anyLong())).thenReturn(portfolio);

        targetService.create(createTargetDTO);

        verify(targetRepository, times(1)).save(targetCaptor.capture());
        Target targetSaved = targetCaptor.getValue();

        assertThat(targetSaved.getTitle()).isEqualTo("This is the title");
        assertThat(targetSaved.getDescription()).isEqualTo("This is the description");

    }

    @Test
    void should_update_target_by_id() {
        doReturn(target).when(targetService).findById(anyLong());

        targetService.updateById(1L, updateTargetDTO);

        verify(targetRepository, times(1)).save(targetCaptor.capture());
        Target targetSaved = targetCaptor.getValue();

        assertThat(targetSaved.getTitle()).isEqualTo("This is an updated title");
        assertThat(targetSaved.getDescription()).isEqualTo("This is an updated description");


    }
}
