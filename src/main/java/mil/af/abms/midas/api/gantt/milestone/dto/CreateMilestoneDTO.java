package mil.af.abms.midas.api.gantt.milestone.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.gantt.GanttInterfaceDTO;
import mil.af.abms.midas.api.validation.PortfolioExists;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMilestoneDTO implements GanttInterfaceDTO {

    @NotNull(message = "Please enter a due date")
    private LocalDate dueDate;

    @NotBlank(message = "Please enter a milestone title")
    private String title;
    private String description;

    @PortfolioExists
    private Long portfolioId;
}
