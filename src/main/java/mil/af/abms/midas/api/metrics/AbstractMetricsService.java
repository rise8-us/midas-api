package mil.af.abms.midas.api.metrics;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.exception.EntityNotFoundException;

public abstract class AbstractMetricsService<E extends AbstractMetricsEntity<D>, D extends AbstractDTO, R extends MetricsCRUDRepository<E, D>> implements MetricsCRUDService<E, D> {

    protected final R repository;

    protected final Class<E> entityClass;

    protected final Class<D> dtoClass;

    @Autowired
    protected AbstractMetricsService(R repository, Class<E> entityClass, Class<D> dtoClass) {
        this.repository = repository;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public Optional<E> getById(LocalDate id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    @Override
    @Transactional
    public E findByIdOrNull(LocalDate id) { return getById(id).orElse(null); }

    @Override
    @Transactional
    public E findById(LocalDate id) {
        return getById(id).orElseThrow(() -> new EntityNotFoundException(entityClass.getSimpleName()));
    }

    @Override
    @Transactional
    public Page<E> search(Specification<E> specs, Integer page, Integer size, String sortBy, String orderBy) {
        List<String> sortOptions = List.of("ASC", "DESC");
        page = Optional.ofNullable(page).orElse(0);
        size = Optional.ofNullable(size).orElse(Integer.MAX_VALUE);
        sortBy = Optional.ofNullable(sortBy).orElse("id");
        orderBy = Optional.ofNullable(orderBy).orElse("ASC");
        Sort.Direction direction = sortOptions.contains(orderBy.toUpperCase()) ? Sort.Direction.valueOf(orderBy) : Sort.Direction.ASC;

        PageRequest pageRequest = PageRequest.of(page, size, direction, sortBy);
        return repository.findAll(Specification.where(specs), pageRequest);
    }

    @Transactional
    public List<D> preparePageResponse(Page<E> page, HttpServletResponse response) {
        response.addHeader("X-Total-Pages", String.valueOf(page.getTotalPages()));
        return toDTOs(page.getContent());
    }

    protected List<D> toDTOs(List<E> entities) {
        return entities.stream().map(E::toDto).collect(Collectors.toList());
    }
}
