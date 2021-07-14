package mil.af.abms.midas.api;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

public interface CRUDService<E extends AbstractEntity<D>, D extends AbstractDTO> {

    Boolean existsById(Long id);

    E findById(Long id);

    E findByIdOrNull(Long id);

    Page<E> search(Specification<E> specs, Integer page, Integer size, String sortBy, String orderBy);

    List<D> preparePageResponse(Page<E> page, HttpServletResponse response);

    void deleteById(Long id);

    void deleteAll();

}
