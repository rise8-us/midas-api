package mil.af.abms.midas.api.gantt.target.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetDTO implements AbstractDTO {

    private Long id;
    private LocalDateTime creationDate;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String title;
    private String description;
    private Long portfolioId;
}
