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
import java.util.List;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.deliverable.DeliverableService;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.gantt.target.dto.CreateTargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.UpdateTargetDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.Product;
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
    @MockBean
    EpicService epicService;
    @MockBean
    DeliverableService deliverableService;

    @Captor
    private ArgumentCaptor<Target> targetCaptor;
    @Captor
    ArgumentCaptor<Long> longCaptor;

    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .get();
    private final Product product = Builder.build(Product.class)
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
            .with(t -> t.setChildren(List.of()))
            .with(t -> t.setEpics(Set.of()))
            .with(t -> t.setDeliverables(Set.of()))
            .with(t -> t.setIsPriority(false))
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
            .with(t -> t.setEpicIds(Set.of(1L)))
            .with(t -> t.setDeliverableIds(Set.of(1L)))
            .get();
    private final UpdateTargetDTO updateTargetDTO = Builder.build(UpdateTargetDTO.class)
            .with(t -> t.setStartDate(target.getStartDate()))
            .with(t -> t.setDueDate(target.getDueDate()))
            .with(t -> t.setTitle("This is an updated title"))
            .with(t -> t.setDescription("This is an updated description"))
            .with(t -> t.setEpicIds(Set.of()))
            .with(t -> t.setDeliverableIds(Set.of()))
            .with(t -> t.setIsPriority(true))
            .get();
    private final Epic epicWithProduct = Builder.build(Epic.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setProduct(product))
            .get();
    private final Epic epicWithPortfolio = Builder.build(Epic.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setPortfolio(portfolio))
            .get();


    @ParameterizedTest
    @CsvSource(value = { "true", "false" })
    void should_create_target(Boolean foundEpic) {
        doReturn(portfolio).when(portfolioService).findById(anyLong());
        when(portfolioService.findById(anyLong())).thenReturn(portfolio);
        doReturn(target).when(targetRepository).save(any());
        doReturn(target).when(targetService).findByIdOrNull(any());
        if (foundEpic) {
            doReturn(epicWithProduct).when(epicService).findByIdOrNull(anyLong());
        } else {
            doReturn(null).when(epicService).findByIdOrNull(anyLong());
        }

        targetService.create(createTargetDTO);

        verify(targetRepository, times(1)).save(targetCaptor.capture());
        Target targetSaved = targetCaptor.getValue();

        assertThat(targetSaved.getTitle()).isEqualTo("This is the title");
        assertThat(targetSaved.getDescription()).isEqualTo("This is the description");
        assertThat(targetSaved.getIsPriority()).isEqualTo(false);

    }

    @Test
    void should_update_target_by_id() {
        targetParent.setChildren(List.of(targetChild));

        var newTarget = new Target();
        BeanUtils.copyProperties(targetParent, newTarget);
        newTarget.setTitle("This is an updated title");
        newTarget.setDescription("This is an updated description");
        newTarget.setId(10L);
        newTarget.setIsPriority(true);

        when(targetRepository.findById(1L)).thenReturn(Optional.of(newTarget));
        when(targetRepository.save(newTarget)).thenReturn(newTarget);
        when(targetRepository.save(targetChild)).thenReturn(targetChild);
        doNothing().when(targetService).updateChildrenDates(any(), any());

        doReturn(newTarget).when(targetService).create(any());

        targetService.updateById(1L, updateTargetDTO);

        verify(targetRepository, times(1)).save(targetCaptor.capture());
        var targetSaved = targetCaptor.getValue();

        assertThat(targetSaved.getTitle()).isEqualTo(updateTargetDTO.getTitle());
        assertThat(targetSaved.getDescription()).isEqualTo(updateTargetDTO.getDescription());
        assertThat(targetSaved.getStartDate()).isEqualTo(updateTargetDTO.getStartDate());
        assertThat(targetSaved.getDueDate()).isEqualTo(updateTargetDTO.getDueDate());
        assertThat(targetSaved.getIsPriority()).isEqualTo(updateTargetDTO.getIsPriority());
    }

    @Test
    void should_update_children_dates() {
        targetChild.setStartDate(LocalDate.now().plusDays(30));
        targetChild.setStartDate(LocalDate.now().plusDays(35));
        targetParent.setChildren(List.of(targetChild));

        when(targetRepository.findById(1L)).thenReturn(Optional.of(targetParent));
        when(targetRepository.save(targetParent)).thenReturn(targetParent);
        when(targetRepository.save(targetChild)).thenReturn(targetChild);

        targetService.updateChildrenDates(updateTargetDTO, targetParent);

        verify(targetRepository, times(1)).save(targetCaptor.capture());
        var targetSaved = targetCaptor.getValue();

        targetSaved.getChildren().forEach(child -> {
            assertThat(child.getStartDate()).isEqualTo(updateTargetDTO.getStartDate());
            assertThat(child.getDueDate()).isEqualTo(updateTargetDTO.getDueDate());
        });
    }

    @Test
    void should_linkGitlabEpic_for_product() {
        doReturn(epicWithProduct).when(epicService).findByIdOrNull(anyLong());
        doReturn(epicWithProduct).when(epicService).updateByIdForProduct(anyLong());

        targetService.linkGitlabEpic(1L, target);

        assertThat(target.getEpics()).isEqualTo(Set.of(epicWithProduct));
    }

    @Test
    void should_linkGitlabEpic_for_portfolio() {
        doReturn(epicWithPortfolio).when(epicService).findByIdOrNull(anyLong());
        doReturn(epicWithPortfolio).when(epicService).updateByIdForPortfolio(anyLong());

        targetService.linkGitlabEpic(1L, target);

        assertThat(target.getEpics()).isEqualTo(Set.of(epicWithPortfolio));
    }

    @Test
    void should_delete_tree() {
        targetParent.setChildren(List.of(targetChild));

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
