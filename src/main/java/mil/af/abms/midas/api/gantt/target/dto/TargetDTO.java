package mil.af.abms.midas.api.gantt.target.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetDTO implements AbstractDTO {

    private Long id;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String title;
    private String description;
    private Long portfolioId;
    private Long parentId;
    private List<TargetDTO> children;
}
