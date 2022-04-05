package mil.af.abms.midas.api.gantt.target.dto;

import javax.validation.constraints.NotBlank;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTargetDTO implements TargetInterfaceDTO {

    private LocalDate startDate;
    private LocalDate dueDate;

    @NotBlank(message = "Please enter a target title")
    private String title;
    private String description;
}
