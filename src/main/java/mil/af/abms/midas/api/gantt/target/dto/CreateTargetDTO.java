package mil.af.abms.midas.api.gantt.target.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.validation.IsValidGanttDueDate;
import mil.af.abms.midas.api.validation.PortfolioExists;
import mil.af.abms.midas.api.validation.TargetExists;

@Data
@IsValidGanttDueDate
@AllArgsConstructor
@NoArgsConstructor
public class CreateTargetDTO implements TargetInterfaceDTO {

    @NotNull(message = "Please enter a start date")
    private LocalDate startDate;
    @NotNull(message = "Please enter a due date")
    private LocalDate dueDate;

    @NotBlank(message = "Please enter a target title")
    private String title;
    private String description;

    @PortfolioExists
    private Long portfolioId;

    @TargetExists
    private Long parentId;

}
