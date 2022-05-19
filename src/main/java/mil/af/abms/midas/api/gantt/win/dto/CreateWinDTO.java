package mil.af.abms.midas.api.gantt.win.dto;

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
public class CreateWinDTO implements GanttInterfaceDTO {

    @NotNull(message = "Please enter a date for this win")
    private LocalDate dueDate;

    @NotBlank(message = "Please enter a title for this win")
    private String title;
    private String description;

    @PortfolioExists
    private Long portfolioId;
}
