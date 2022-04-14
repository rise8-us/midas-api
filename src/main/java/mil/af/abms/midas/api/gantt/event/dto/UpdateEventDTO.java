package mil.af.abms.midas.api.gantt.event.dto;

import javax.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDTO implements EventInterfaceDTO {

    private LocalDate startDate;
    private LocalDate dueDate;

    @NotBlank(message = "Please enter an event title")
    private String title;
    private String description;
    private String location;

    private Set<Long> organizerIds;
}
