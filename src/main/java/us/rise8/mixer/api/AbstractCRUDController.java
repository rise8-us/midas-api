package us.rise8.mixer.api;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import us.rise8.mixer.api.search.annotation.SearchSpec;
import us.rise8.mixer.config.auth.IsAdmin;

@CrossOrigin
public abstract class AbstractCRUDController<
        E extends AbstractEntity<D>,
        D extends AbstractDTO,
        S extends CRUDService<E, D>
        > implements CRUDController<D, E> {

    protected final S service;

    @Autowired
    protected AbstractCRUDController(S service) {
        this.service = service;
    }

    @Override
    public D getById(Long id) {
        return service.findById(id);
    }

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

    @Override
    @IsAdmin
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }
}
