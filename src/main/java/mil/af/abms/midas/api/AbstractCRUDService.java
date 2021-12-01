package mil.af.abms.midas.api;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import mil.af.abms.midas.exception.EntityNotFoundException;

public abstract class AbstractCRUDService<E extends AbstractEntity<D>, D extends AbstractDTO, R extends RepositoryInterface<E, D>> implements CRUDService<E, D> {

    protected final R repository;

    protected final Class<E> entityClass;

    protected final Class<D> dtoClass;

    @Autowired
    protected AbstractCRUDService(R repository, Class<E> entityClass, Class<D> dtoClass) {
        this.repository = repository;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    @Override
    @Transactional
    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public E findById(Long id) {
        return getById(id).orElseThrow(() -> new EntityNotFoundException(entityClass.getSimpleName(), id));
    }

    public Optional<E> getById(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id);
    }

    @Override
    @Transactional
    public E findByIdOrNull(Long id) { return getById(id).orElse(null); }

    @Override
    @Transactional
    public Page<E> search(Specification<E> specs, Integer page, Integer size, String sortBy, String orderBy) {
        List<String> sortOptions = List.of("ASC", "DESC");
        page = Optional.ofNullable(page).orElse(0);
        size = Optional.ofNullable(size).orElse(Integer.MAX_VALUE);
        sortBy = Optional.ofNullable(sortBy).orElse("id");
        orderBy = Optional.ofNullable(orderBy).orElse("ASC");
        Direction direction = sortOptions.contains(orderBy.toUpperCase()) ? Direction.valueOf(orderBy) : Direction.ASC;

        var pageRequest = PageRequest.of(page, size, direction, sortBy);
        return repository.findAll(Specification.where(specs), pageRequest);
    }

    @Override
    @Transactional
    public List<D> preparePageResponse(Page<E> page, HttpServletResponse response) {
        response.addHeader("X-Total-Pages", String.valueOf(page.getTotalPages()));
        return toDTOs(page.getContent());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.delete(findById(id));
    }

    @Override
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    protected List<D> toDTOs(List<E> entities) {
        return entities.stream().map(AbstractEntity::toDto).collect(Collectors.toList());
    }

}
