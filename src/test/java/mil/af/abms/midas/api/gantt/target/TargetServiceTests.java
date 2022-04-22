package mil.af.abms.midas.api.gantt.target;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.comment.Comment;
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
    private ArgumentCaptor<Target> targetCaptor;
    @Captor
    ArgumentCaptor<Long> longCaptor;

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
            .with(t -> t.setParent(null))
            .with(t -> t.setChildren(Set.of()))
            .get();
    private final Target targetParent = Builder.build(Target.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setStartDate(LocalDate.now()))
            .with(t -> t.setDueDate(LocalDate.now().plusDays(1)))
            .with(t -> t.setTitle("This is the title"))
            .with(t -> t.setDescription("This is the description"))
            .with(t -> t.setPortfolio(portfolio))
            .with(t -> t.setParent(null))
            .get();
    private final Target targetChild = Builder.build(Target.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setStartDate(LocalDate.now()))
            .with(t -> t.setDueDate(LocalDate.now().plusDays(1)))
            .with(t -> t.setTitle("This is the title"))
            .with(t -> t.setDescription("This is the description"))
            .with(t -> t.setPortfolio(portfolio))
            .with(t -> t.setParent(targetParent))
            .get();
    private final CreateTargetDTO createTargetDTO = Builder.build(CreateTargetDTO.class)
            .with(t -> t.setStartDate(target.getStartDate()))
            .with(t -> t.setDueDate(target.getDueDate()))
            .with(t -> t.setTitle(target.getTitle()))
            .with(t -> t.setDescription(target.getDescription()))
            .with(t -> t.setPortfolioId(target.getPortfolio().getId()))
            .with(t -> t.setParentId(null))
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
        doReturn(target).when(targetRepository).save(any());
        doReturn(target).when(targetService).findByIdOrNull(any());

        targetService.create(createTargetDTO);

        verify(targetRepository, times(1)).save(targetCaptor.capture());
        Target targetSaved = targetCaptor.getValue();

        assertThat(targetSaved.getTitle()).isEqualTo("This is the title");
        assertThat(targetSaved.getDescription()).isEqualTo("This is the description");

    }

    @Test
    void should_update_target_by_id() {
        targetParent.setChildren(Set.of(targetChild));

        var newTarget = new Target();
        BeanUtils.copyProperties(targetParent, newTarget);
        newTarget.setTitle("This is an updated title");
        newTarget.setDescription("This is an updated description");
        newTarget.setId(10L);

        when(targetRepository.findById(1L)).thenReturn(Optional.of(newTarget));
        when(targetRepository.save(newTarget)).thenReturn(newTarget);
        when(targetRepository.save(targetChild)).thenReturn(targetChild);

        doReturn(newTarget).when(targetService).create(any());

        targetService.updateById(1L, updateTargetDTO);

        verify(targetRepository, times(1)).save(targetCaptor.capture());
        var targetSaved = targetCaptor.getValue();

        assertThat(targetSaved.getTitle()).isEqualTo(updateTargetDTO.getTitle());
        assertThat(targetSaved.getDescription()).isEqualTo(updateTargetDTO.getDescription());
        assertThat(targetSaved.getStartDate()).isEqualTo(updateTargetDTO.getStartDate());
        assertThat(targetSaved.getDueDate()).isEqualTo(updateTargetDTO.getDueDate());
    }

    @Test
    void should_delete_tree() {
        targetParent.setChildren(Set.of(targetChild));

        doReturn(targetParent).when(targetService).findById(1L);
        doReturn(targetChild).when(targetService).findById(2L);
        doNothing().when(targetRepository).deleteById(1L);
        doNothing().when(targetRepository).deleteById(2L);

        targetService.deleteById(1L);

        verify(targetRepository, times(2)).deleteById(longCaptor.capture());

        Long childId = longCaptor.getAllValues().get(0);
        Long parentId = longCaptor.getAllValues().get(1);

        assertThat(childId).isEqualTo(2L);
        assertThat(parentId).isEqualTo(1L);
    }

    @Test
    void should_recursively_deleteById() {
        targetParent.getChildren().add(targetChild);
        Comment comment = Builder.build(Comment.class).with(c -> c.setId(5L)).get();

        doReturn(targetParent).when(targetService).findById(targetParent.getId());
        doReturn(targetChild).when(targetService).findById(targetChild.getId());
        doNothing().when(targetRepository).deleteById(any());

        targetService.deleteById(this.targetParent.getId());

        verify(targetRepository, times(2)).deleteById(longCaptor.capture());

        assertThat(longCaptor.getAllValues().get(0)).isEqualTo(2L);
        assertThat(longCaptor.getAllValues().get(1)).isEqualTo(1L);
    }
}