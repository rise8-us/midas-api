package mil.af.abms.midas.api.metrics.metricspageview;

import org.springframework.stereotype.Repository;

import mil.af.abms.midas.api.metrics.MetricsCRUDRepository;
import mil.af.abms.midas.api.metrics.dtos.MetricsPageViewDTO;

@Repository
public interface MetricsPageViewRepository extends MetricsCRUDRepository<MetricsPageView, MetricsPageViewDTO> {
}
