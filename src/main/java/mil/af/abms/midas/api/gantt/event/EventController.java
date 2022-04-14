package mil.af.abms.midas.api.gantt.event;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.gantt.event.dto.CreateEventDTO;
import mil.af.abms.midas.api.gantt.event.dto.EventDTO;
import mil.af.abms.midas.api.gantt.event.dto.UpdateEventDTO;
import mil.af.abms.midas.config.security.annotations.HasEventCreateAccess;
import mil.af.abms.midas.config.security.annotations.HasEventModifyAccess;

@RestController
@RequestMapping("/api/gantt_events")
public class EventController extends AbstractCRUDController<Event, EventDTO, EventService> {

    @Autowired
    public EventController(EventService service) {
        super(service);
    }

    @HasEventCreateAccess
    @PostMapping
    public EventDTO create(@Valid @RequestBody CreateEventDTO createEventDTO) {
        return service.create(createEventDTO).toDto();
    }

    @HasEventModifyAccess
    @PutMapping("/{id}")
    public EventDTO updateById(@Valid @RequestBody UpdateEventDTO updateEventDTO, @PathVariable Long id) {
        return service.updateById(id, updateEventDTO).toDto();
    }

    @HasEventModifyAccess
    @Override
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

}
