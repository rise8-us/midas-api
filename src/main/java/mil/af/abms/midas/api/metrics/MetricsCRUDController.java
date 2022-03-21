package mil.af.abms.midas.api.metrics;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.search.annotation.SearchSpec;


public interface MetricsCRUDController<D extends AbstractDTO, E extends AbstractMetricsEntity<D>> {

    @GetMapping("/{id}")
    D getById(@PathVariable String id);

    @GetMapping
    List<D> search(
            HttpServletResponse response,
            @SearchSpec Specification<E> specs,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, value = "sort_by") String sortBy,
            @RequestParam(required = false, value = "order_by") String orderBy
    );

}
