package mil.af.abms.midas.api.metrics;

import javax.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.search.annotation.SearchSpec;

@CrossOrigin
public abstract class AbstractMetricsController<E extends AbstractMetricsEntity<D>, D extends AbstractDTO, S extends MetricsCRUDService<E, D>> implements MetricsCRUDController<D, E> {

    protected final S service;

    @Autowired
    public AbstractMetricsController(S service) { this.service = service; }

    @Override
    public List<D> search(
            HttpServletResponse response,
            @SearchSpec Specification<E> specs,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, value = "sort_by") String sortBy,
            @RequestParam(required = false, value = "order_by") String orderBy
    ) {
        Page<E> fetchedPage = service.search(specs, page, size, sortBy, orderBy);
        return service.preparePageResponse(fetchedPage, response);
    }

    public D getById(String id) {
        return service.findById(LocalDate.parse(id)).toDto();
    }

}
