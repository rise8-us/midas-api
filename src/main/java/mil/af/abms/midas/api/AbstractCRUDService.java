package mil.af.abms.midas.api;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import mil.af.abms.midas.exception.EntityNotFoundException;

public abstract class AbstractCRUDService<E extends AbstractEntity<D>, D extends AbstractDTO, R extends RepositoryInterface<E, D>> implements CRUDService<E, D> {

    protected final R repository;

    protected final Class<E> entityClass;

    protected final Class<D> dtoClass;

    @Autowired
    public AbstractCRUDService(R repository, Class<E> entityClass, Class<D> dtoClass) {
        this.repository = repository;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    @Override
    @Transactional
    public Boolean existsById(Long id) {
        try {
            getObject(id);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    @Override
    @Transactional
    public E getObject(Long id) {
        if (id == null) {
            throw new EntityNotFoundException(entityClass.getSimpleName());
        }
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(entityClass.getSimpleName(), id));
    }

    @Override
    @Transactional
    public E findById(Long id) {
        return getObject(id);
    }

    @Override
    @Transactional
    public Page<E> search(Specification<E> specs, Integer page, Integer size, String sortBy, String orderBy) {
        if (page == null || size == null) {
            page = 0;
            size = Integer.MAX_VALUE;
        }

        if (sortBy == null) {
            sortBy = "id";
        }

        if (orderBy == null) {
            orderBy = "asc";
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(orderBy.equals("desc") ? Direction.DESC : Direction.ASC, sortBy));
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
        repository.delete(getObject(id));
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
