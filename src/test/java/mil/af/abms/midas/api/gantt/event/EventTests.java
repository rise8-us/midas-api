package mil.af.abms.midas.api.gantt.event;

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

import mil.af.abms.midas.api.gantt.event.dto.EventDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.user.User;

class EventTests {
    private static final int ENTITY_DTO_FIELD_OFFSET = 1;

    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .get();
    private final User user1 = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .get();
    private final Event event = Builder.build(Event.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setStartDate(LocalDate.now()))
            .with(e -> e.setDueDate(LocalDate.now().plusDays(1)))
            .with(e -> e.setTitle("eventTitle"))
            .with(e -> e.setDescription("eventDescription"))
            .with(e -> e.setPortfolio(portfolio))
            .with(e -> e.setLocation("Here"))
            .with(e -> e.setOrganizers(Set.of(user1)))
            .with(e -> e.setAttendees(Set.of(user1)))
            .get();
    private final EventDTO eventDTO = Builder.build(EventDTO.class)
            .with(e -> e.setId(event.getId()))
            .with(e -> e.setStartDate(event.getStartDate()))
            .with(e -> e.setDueDate(event.getDueDate()))
            .with(e -> e.setTitle(event.getTitle()))
            .with(e -> e.setDescription(event.getDescription()))
            .with(e -> e.setPortfolioId(event.getPortfolio().getId()))
            .with(e -> e.setLocation(event.getLocation()))
            .with(e -> e.setOrganizerIds(Set.of(1L)))
            .with(e -> e.setAttendeeIds(Set.of(1L)))
            .get();
    @Test
    void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Event.class, fields::add);

        assertThat(fields.size()).isEqualTo(EventDTO.class.getDeclaredFields().length  + ENTITY_DTO_FIELD_OFFSET);
    }

    @Test
    void should_get_properties() {
        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(event.getDueDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(event.getTitle()).isEqualTo("eventTitle");
        assertThat(event.getDescription()).isEqualTo("eventDescription");
        assertThat(event.getPortfolio()).isEqualTo(portfolio);
        assertThat(event.getLocation()).isEqualTo("Here");
    }

    @Test
    void should_be_equal() {
        Event event2 = new Event();
        BeanUtils.copyProperties(event, event2);

        assertEquals(event, event);
        assertNotEquals(null, event);
        assertNotEquals(event, new User());
        assertNotEquals(event, new Event());
        assertEquals(event, event2);
        assertFalse(event.equals(null));
    }

    @Test
    void can_return_dto() { assertThat(event.toDto()).isEqualTo(eventDTO); }

}
