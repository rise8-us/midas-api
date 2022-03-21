package mil.af.abms.midas.api.metrics;

import javax.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import mil.af.abms.midas.api.AbstractDTO;

public interface MetricsCRUDService<E extends AbstractMetricsEntity<D>, D extends AbstractDTO> {

    E findById(LocalDate id);

    E findByIdOrNull(LocalDate id);

    Page<E> search(Specification<E> specs, Integer page, Integer size, String sortBy, String orderBy);

    List<D> preparePageResponse(Page<E> page, HttpServletResponse response);
}
