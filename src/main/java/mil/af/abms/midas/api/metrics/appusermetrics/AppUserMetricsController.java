package mil.af.abms.midas.api.metrics.appusermetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.metrics.AbstractMetricsController;
import mil.af.abms.midas.api.metrics.appusermetrics.dto.AppUserMetricsDTO;

@RestController
@RequestMapping("/api/appUserMetrics")
public class AppUserMetricsController extends AbstractMetricsController<AppUserMetrics, AppUserMetricsDTO, AppUserMetricsService> {
    @Autowired
    public AppUserMetricsController(AppUserMetricsService service) { super(service); }
}
