package mil.af.abms.midas.api.gantt.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.gantt.AbstractGanttEntity;
import mil.af.abms.midas.api.gantt.event.dto.EventDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.user.User;

@Entity @Getter @Setter
@Table(name = "gantt_event")
public class Event extends AbstractGanttEntity<EventDTO> {

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "gantt_portfolio_event",
            joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"))
    private Portfolio portfolio;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String location;

    @ManyToMany
    @JoinTable(
            name = "gantt_event_user_organizer",
            joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> organizers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "gantt_event_user_attendee",
            joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> attendees = new HashSet<>();

    public EventDTO toDto() {
        return new EventDTO(
                id,
                startDate,
                dueDate,
                title,
                description,
                getIdOrNull(portfolio),
                location,
                getIds(organizers),
                getIds(attendees)
        );
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event that = (Event) o;
        return this.hashCode() == that.hashCode();
    }
}
