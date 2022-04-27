package mil.af.abms.midas.api.gantt.event.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO implements AbstractDTO {

    private Long id;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String title;
    private String description;
    private Long portfolioId;
    private String location;
    private Set<Long> organizerIds;
    private Set<Long> attendeeIds;
}
