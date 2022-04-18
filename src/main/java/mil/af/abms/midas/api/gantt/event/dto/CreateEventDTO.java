package mil.af.abms.midas.api.gantt.event.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.validation.IsValidGanttDueDate;
import mil.af.abms.midas.api.validation.PortfolioExists;

@Data
@IsValidGanttDueDate
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventDTO implements EventInterfaceDTO {

    @NotNull(message = "Please enter a start date")
    private LocalDate startDate;
    @NotNull(message = "Please enter a due date")
    private LocalDate dueDate;

    @NotBlank(message = "Please enter an event title")
    private String title;
    private String description;

    @PortfolioExists
    private Long portfolioId;
    private String location;

    private Set<Long> organizerIds;
}
