package mil.af.abms.midas.api.metrics.metricspageview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdatePageViewsDTO implements AbstractDTO {
    private String pathname;
}
