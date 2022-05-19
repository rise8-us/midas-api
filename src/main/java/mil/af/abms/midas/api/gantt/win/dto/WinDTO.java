package mil.af.abms.midas.api.gantt.win.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WinDTO implements AbstractDTO {

    private Long id;
    private LocalDate dueDate;
    private String title;
    private String description;
    private Long portfolioId;
}
