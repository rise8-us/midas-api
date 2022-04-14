package mil.af.abms.midas.api.gantt.event;

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

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.gantt.event.dto.CreateEventDTO;
import mil.af.abms.midas.api.gantt.event.dto.UpdateEventDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@WebMvcTest({EventController.class})
public class EventControllerTests extends ControllerTestHarness {

    @MockBean
    private EventService eventService;
    @MockBean
    private PortfolioService portfolioService;

    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .get();
    private final Event event = Builder.build(Event.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setStartDate(LocalDate.now()))
            .with(t -> t.setDueDate(LocalDate.now().plusDays(1)))
            .with(t -> t.setTitle("eventTitle"))
            .with(t -> t.setDescription("eventDescription"))
            .with(t -> t.setPortfolio(portfolio))
            .with(e -> e.setLocation("Here"))
            .get();
    private final CreateEventDTO createEventDTO = Builder.build(CreateEventDTO.class)
            .with(t -> t.setStartDate(event.getStartDate()))
            .with(t -> t.setDueDate(event.getDueDate()))
            .with(t -> t.setTitle(event.getTitle()))
            .with(t -> t.setDescription(event.getDescription()))
            .with(t -> t.setPortfolioId(event.getPortfolio().getId()))
            .with(e -> e.setLocation("Here"))
            .get();
    private final UpdateEventDTO updateEventDTO = Builder.build(UpdateEventDTO.class)
            .with(t -> t.setStartDate(event.getStartDate()))
            .with(t -> t.setDueDate(event.getDueDate()))
            .with(t -> t.setTitle("This is an updated title"))
            .with(t -> t.setDescription("This is an updated description"))
            .with(e -> e.setLocation("There"))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_event() throws Exception {
        doReturn(event).when(eventService).create(any(CreateEventDTO.class));
        doReturn(true).when(portfolioService).existsById(anyLong());

        mockMvc.perform(post("/api/gantt_events")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createEventDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("eventTitle"));
    }

    @Test
    void should_update_by_id() throws Exception {
        var newEvent = new Event();
        BeanUtils.copyProperties(updateEventDTO, newEvent);
        when(eventService.updateById(any(), any(UpdateEventDTO.class))).thenReturn(newEvent);

        mockMvc.perform(put("/api/gantt_events/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateEventDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("This is an updated title"))
                .andExpect(jsonPath("$.description").value("This is an updated description"));
    }

    @Test
    void should_throw_errors_due_to_null_parameters_on_create() throws Exception {
        var event2 = new CreateEventDTO();
        BeanUtils.copyProperties(createEventDTO, event2);
        event2.setTitle(null);
        event2.setPortfolioId(null);

        mockMvc.perform(post("/api/gantt_events")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(event2))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").exists())
                .andExpect(jsonPath("$.errors[2]").doesNotExist());
    }

    @Test
    void should_throw_errors_due_to_null_parameters_on_update() throws Exception {
        var event3 = new UpdateEventDTO();
        BeanUtils.copyProperties(updateEventDTO, event3);
        event3.setTitle(null);

        mockMvc.perform(put("/api/gantt_events/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(event3))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").doesNotExist());
    }

    @Test
    void should_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/gantt_events/1"))
                .andExpect(status().isOk());

        verify(eventService, times(1)).deleteById(1L);
    }

}
