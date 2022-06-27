package mil.af.abms.midas.api.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SprintProductMetricsDTO implements AbstractDTO {

    private LocalDate date;
    private Long deliveredPoints;
    private Integer deliveredStories;
    private Float releaseFrequency;

}
