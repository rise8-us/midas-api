package mil.af.abms.midas.api.metrics.appusermetrics;

import org.springframework.stereotype.Repository;

import mil.af.abms.midas.api.metrics.MetricsCRUDRepository;
import mil.af.abms.midas.api.metrics.appusermetrics.dto.AppUserMetricsDTO;

@Repository
public interface AppUserMetricsRepository extends MetricsCRUDRepository<AppUserMetrics, AppUserMetricsDTO> {
}
