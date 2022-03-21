package mil.af.abms.midas.api.metrics.dtos;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsPageViewDTO implements AbstractDTO {
    private LocalDate id;
    private Map<String, Set<Long>> pageViews;
}
