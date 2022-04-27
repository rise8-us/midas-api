package mil.af.abms.midas.api.gantt.event;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.gantt.event.dto.CreateEventDTO;
import mil.af.abms.midas.api.gantt.event.dto.UpdateEventDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;

@ExtendWith(SpringExtension.class)
@Import(EventService.class)
public class EventServiceTests {

    @SpyBean
    EventService eventService;
    @MockBean
    SimpMessageSendingOperations websocket;
    @MockBean
    UserService userService;
    @MockBean
    PortfolioService portfolioService;
    @MockBean
    EventRepository eventRepository;
    @Captor
    ArgumentCaptor<Event> eventCaptor;
    @Captor
    ArgumentCaptor<Long> longCaptor;

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
    private final CreateEventDTO createEventDTO = Builder.build(CreateEventDTO.class)
            .with(e -> e.setStartDate(event.getStartDate()))
            .with(e -> e.setDueDate(event.getDueDate()))
            .with(e -> e.setTitle(event.getTitle()))
            .with(e -> e.setDescription(event.getDescription()))
            .with(e -> e.setPortfolioId(event.getPortfolio().getId()))
            .with(e -> e.setLocation("Here"))
            .with(e -> e.setOrganizerIds(Set.of(1L)))
            .with(e -> e.setAttendeeIds(Set.of(1L)))
            .get();
    private final UpdateEventDTO updateEventDTO = Builder.build(UpdateEventDTO.class)
            .with(e -> e.setStartDate(event.getStartDate()))
            .with(e -> e.setDueDate(event.getDueDate()))
            .with(e -> e.setTitle("This is an updated title"))
            .with(e -> e.setDescription("This is an updated description"))
            .with(e -> e.setLocation("There"))
            .with(e -> e.setOrganizerIds(Set.of(1L)))
            .with(e -> e.setAttendeeIds(Set.of(1L)))
            .get();

    @Test
    void should_create_event() {
        doReturn(portfolio).when(portfolioService).findById(anyLong());
        when(portfolioService.findById(anyLong())).thenReturn(portfolio);

        eventService.create(createEventDTO);

        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event eventSaved = eventCaptor.getValue();

        assertThat(eventSaved.getTitle()).isEqualTo("eventTitle");
        assertThat(eventSaved.getDescription()).isEqualTo("eventDescription");

    }

    @Test
    void should_update_event_by_id() {
        doReturn(event).when(eventService).findById(anyLong());

        eventService.updateById(1L, updateEventDTO);

        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event eventSaved = eventCaptor.getValue();

        assertThat(eventSaved.getTitle()).isEqualTo("This is an updated title");
        assertThat(eventSaved.getDescription()).isEqualTo("This is an updated description");
        assertThat(eventSaved.getLocation()).isEqualTo("There");
    }

    @Test
    void should_delete_event_by_id() {
        doReturn(event).when(eventService).findById(anyLong());

        eventService.deleteById(1L);

        verify(eventRepository, times(1)).deleteById(longCaptor.capture());

        assertThat(longCaptor.getValue()).isEqualTo(1L);
    }
}
