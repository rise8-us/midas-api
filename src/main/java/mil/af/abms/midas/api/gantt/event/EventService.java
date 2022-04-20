package mil.af.abms.midas.api.gantt.event;

import javax.transaction.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.gantt.event.dto.CreateEventDTO;
import mil.af.abms.midas.api.gantt.event.dto.EventDTO;
import mil.af.abms.midas.api.gantt.event.dto.EventInterfaceDTO;
import mil.af.abms.midas.api.gantt.event.dto.UpdateEventDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.user.UserService;

@Service
public class EventService extends AbstractCRUDService<Event, EventDTO, EventRepository> {

    private PortfolioService portfolioService;
    private UserService userService;

    public EventService(EventRepository repository) {
        super(repository, Event.class, EventDTO.class);
    }

    @Autowired
    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    public Event create(CreateEventDTO dto) {
        Event newEvent = Builder.build(Event.class)
                .with(e -> e.setPortfolio(portfolioService.findById(dto.getPortfolioId())))
                .get();

        updateCommonFields(dto, newEvent);

        return repository.save(newEvent);
    }

    @Transactional
    public Event updateById(Long id, UpdateEventDTO dto) {
        Event foundEvent = findById(id);

        updateCommonFields(dto, foundEvent);

        return repository.save(foundEvent);
    }

    protected void updateCommonFields(EventInterfaceDTO dto, Event event) {
        event.setDueDate(dto.getDueDate());
        event.setStartDate(dto.getStartDate());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        Optional.ofNullable(dto.getOrganizerIds()).ifPresent((organizer -> {
            var organizers = organizer.stream().map(userService::findByIdOrNull).collect(Collectors.toSet());
            event.setOrganizers(organizers);
        }));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Event eventToDelete = findById(id);
        removeOrganizers(eventToDelete);
        repository.deleteById(id);
    }

    private void removeOrganizers(Event event) {
        event.setOrganizers(new HashSet<>());
    }

}
