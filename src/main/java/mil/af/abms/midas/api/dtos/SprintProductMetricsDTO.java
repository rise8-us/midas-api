package mil.af.abms.midas.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
public class SprintProductMetricsDTO implements AbstractDTO {

    private String productName;
    private Long deliveredPoints;
    private Integer deliveredStories;

}
