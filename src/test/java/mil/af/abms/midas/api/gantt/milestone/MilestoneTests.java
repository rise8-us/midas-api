package mil.af.abms.midas.api.gantt.milestone;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.gantt.milestone.dto.MilestoneDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.user.User;

public class MilestoneTests {

    private final LocalDate DUE_DATE = TimeConversion.getLocalDateOrNullFromObject("2021-07-09");
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("portfolio name"))
            .with(p -> p.setDescription("description"))
            .get();
    private final Milestone milestone = Builder.build(Milestone.class)
            .with(m -> m.setId(2L))
            .with(m -> m.setDueDate(DUE_DATE))
            .with(m -> m.setTitle("milestoneTitle"))
            .with(m -> m.setDescription("milestoneDescription"))
            .with(m -> m.setPortfolio(portfolio))
            .get();

    private final MilestoneDTO milestoneDTO = Builder.build(MilestoneDTO.class)
            .with(m -> m.setId(2L))
            .with(m -> m.setDueDate(milestone.getDueDate()))
            .with(m -> m.setTitle(milestone.getTitle()))
            .with(m -> m.setDescription(milestone.getDescription()))
            .with(m -> m.setPortfolioId(1L))
            .get();

    @Test
    void should_have_all_milestone_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Milestone.class, fields::add);

        assertThat(fields.size()).isEqualTo(6);
    }

    @Test
    void should_get_properties() {
        assertThat(milestone.getId()).isEqualTo(2L);
        assertThat(milestone.getDueDate()).isEqualTo(DUE_DATE);
        assertThat(milestone.getTitle()).isEqualTo("milestoneTitle");
        assertThat(milestone.getDescription()).isEqualTo("milestoneDescription");
        assertThat(milestone.getPortfolio()).isEqualTo(portfolio);
    }

    @Test
    void should_be_equal() {
        Milestone milestone2 = new Milestone();
        BeanUtils.copyProperties(milestone, milestone2);

        assertEquals(milestone, milestone);
        assertNotEquals(milestone, null);
        assertNotEquals(milestone, new User());
        assertEquals(milestone, milestone2);
    }

    @Test
    void should_return_dto() {
        assertThat(milestone.toDto()).isEqualTo(milestoneDTO);
    }

}
