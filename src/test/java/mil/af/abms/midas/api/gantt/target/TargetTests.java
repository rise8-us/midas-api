package mil.af.abms.midas.api.gantt.target;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.gantt.target.dto.TargetDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.user.User;

class TargetTests {
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
    TargetDTO targetDTO = Builder.build(TargetDTO.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setStartDate(LocalDate.now()))
            .with(t -> t.setDueDate(LocalDate.now().plusDays(1)))
            .with(t -> t.setTitle("This is the title"))
            .with(t -> t.setDescription("This is the description"))
            .with(t -> t.setPortfolioId(portfolio.getId()))
            .with(t -> t.setChildrenIds(List.of()))
            .with(t -> t.setChildrenEpicIds(Set.of()))
            .with(t -> t.setEpicIds(Set.of()))
            .with(t -> t.setDeliverableIds(Set.of()))
            .with(t -> t.setIsPriority(false))
            .get();

    @Test
    void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Target.class, fields::add);

        assertThat(fields).hasSize(TargetDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_get_properties() {
        assertThat(target.getId()).isEqualTo(1L);
        assertThat(target.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(target.getDueDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(target.getTitle()).isEqualTo("This is the title");
        assertThat(target.getDescription()).isEqualTo("This is the description");
        assertThat(target.getPortfolio()).isEqualTo(portfolio);
    }

    @Test
    void should_be_equal() {
        Target target2 = new Target();
        BeanUtils.copyProperties(target, target2);

        assertThat(target).isEqualTo(target);
        assertThat(target).isNotNull();
        assertThat(target).isNotEqualTo(new User());
        assertThat(target).isNotSameAs(new Target());
        assertThat(target).isEqualTo(target2);
    }

    @Test
    void can_return_dto() { assertThat(target.toDto()).isEqualTo(targetDTO); }

    @Test
    void can_return_child_epic_ids() {
        Epic epic1 = Builder.build(Epic.class)
                .with(e -> e.setId(200L))
                .get();
        Epic epic2 = Builder.build(Epic.class)
                .with(e -> e.setId(201L))
                .get();
        Target child = Builder.build(Target.class)
                .with(t -> t.setId(10L))
                .with(t -> t.setEpics(Set.of(epic1, epic2)))
                .get();
        Target target2 = new Target();
        BeanUtils.copyProperties(target, target2);
        target2.setChildren(List.of(child));

        assertThat(target2.toDto().getChildrenEpicIds()).isEqualTo(Set.of(200L, 201L));
    }
}
