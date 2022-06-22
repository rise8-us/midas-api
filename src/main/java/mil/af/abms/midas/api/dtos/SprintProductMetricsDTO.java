package mil.af.abms.midas.api.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
public class SprintProductMetricsDTO implements AbstractDTO {

    private LocalDate date;
    private Long deliveredPoints;
    private Integer deliveredStories;

}
