package mil.af.abms.midas.api.gantt.milestone.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.gantt.GanttInterfaceDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMilestoneDTO implements GanttInterfaceDTO {

    @NotNull(message = "Please enter a due date")
    private LocalDate dueDate;

    @NotBlank(message = "Please enter a milestone title")
    private String title;
    private String description;
}
