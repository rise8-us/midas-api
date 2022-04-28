package mil.af.abms.midas.api.gantt.target.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.gantt.GanttDateInterfaceDTO;
import mil.af.abms.midas.api.validation.IsValidGanttDueDate;

@Data
@IsValidGanttDueDate
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTargetDTO implements GanttDateInterfaceDTO {

    @NotNull(message = "Please enter a start date")
    private LocalDate startDate;
    @NotNull(message = "Please enter a due date")
    private LocalDate dueDate;

    @NotBlank(message = "Please enter a target title")
    private String title;
    private String description;

    private Set<Long> gitlabEpicIds;
    private Set<Long> deliverableIds;
}
