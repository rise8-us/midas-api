package mil.af.abms.midas.api.gantt.target;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.gantt.target.dto.TargetDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.user.User;

class TargetTests {
    private static final int ENTITY_DTO_FIELD_OFFSET = 1;

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
            .with(t -> t.setChildren(List.of()))
            .with(t -> t.setEpics(Set.of()))
            .with(t -> t.setDeliverables(Set.of()))
            .get();

    @Test
    void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Target.class, fields::add);

        assertThat(fields.size()).isEqualTo(TargetDTO.class.getDeclaredFields().length + ENTITY_DTO_FIELD_OFFSET);
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

        assertEquals(target, target);
        assertNotEquals(null, target);
        assertNotEquals(target, new User());
        assertNotEquals(target, new Target());
        assertEquals(target, target2);
        assertFalse(target.equals(null));
    }

    @Test
    void can_return_dto() { assertThat(target.toDto()).isEqualTo(targetDTO); }

}
