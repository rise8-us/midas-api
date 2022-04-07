package mil.af.abms.midas.api.gantt.milestone.dto;

import javax.validation.constraints.NotBlank;

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

    private LocalDate dueDate;

    @NotBlank(message = "Please enter a target title")
    private String title;
    private String description;

    @PortfolioExists
    private Long portfolioId;
}