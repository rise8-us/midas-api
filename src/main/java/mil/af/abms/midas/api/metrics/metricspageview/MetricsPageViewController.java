package mil.af.abms.midas.api.metrics.metricspageview;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.metrics.AbstractMetricsController;
import mil.af.abms.midas.api.metrics.dtos.MetricsPageViewDTO;
import mil.af.abms.midas.api.metrics.metricspageview.dto.CreateOrUpdatePageViewsDTO;

@RestController
@RequestMapping("/api/metrics_page_view")
public class MetricsPageViewController extends AbstractMetricsController<MetricsPageView, MetricsPageViewDTO, MetricsPageViewService> {
    
    @Autowired
    public MetricsPageViewController(MetricsPageViewService service) {
        super(service);
    }

    @PostMapping
    MetricsPageViewDTO createOrUpdate(@Valid @RequestBody CreateOrUpdatePageViewsDTO createOrUpdatePageViewsDTO) {
        return service.determineCreateOrUpdate(createOrUpdatePageViewsDTO).toDto();
    }

}
