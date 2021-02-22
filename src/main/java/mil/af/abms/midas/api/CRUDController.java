package mil.af.abms.midas.api;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

import mil.af.abms.midas.api.search.annotation.SearchSpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


public interface CRUDController<D extends AbstractDTO, E extends AbstractEntity<D>> {

    @GetMapping("/{id}")
    D getById(@PathVariable Long id);

    @GetMapping
    List<D> search(
            HttpServletResponse response,
            @SearchSpec Specification<E> specs,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, value = "sort_by") String sortBy,
            @RequestParam(required = false, value = "order_by") String orderBy
    );

    @DeleteMapping("/{id}")
    void deleteById(@PathVariable Long id);

}
