package mil.af.abms.midas.api.appusermetrics;

import javax.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.appusermetrics.dto.AppUserMetricsDTO;
import mil.af.abms.midas.api.search.annotation.SearchSpec;

@RestController
@CrossOrigin
@RequestMapping("/api/appUserMetrics")
public class AppUserMetricsController {

    private final AppUserMetricsService service;

    public AppUserMetricsController(AppUserMetricsService service) { this.service = service; }

    @GetMapping
    List<AppUserMetricsDTO> search(
            HttpServletResponse response,
            @SearchSpec Specification<AppUserMetrics> specs,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, value = "sort_by") String sortBy,
            @RequestParam(required = false, value = "order_by") String orderBy
    ) {
        Page<AppUserMetrics> fetchedPage = service.search(specs, page, size, sortBy, orderBy);
        return service.preparePageResponse(fetchedPage, response);
    }

    @GetMapping("/{id}")
    AppUserMetricsDTO getById(@PathVariable String id) {
        return service.findById(LocalDate.parse(id)).toDto();
    }

}
