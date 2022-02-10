package mil.af.abms.midas.api.epic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.epic.dto.EpicDTO;

public interface EpicRepository extends RepositoryInterface<Epic, EpicDTO> {
    Optional<Epic> findByEpicUid(String uId);
    Optional<Epic> findByEpicIid(Integer iId);

    @Query(value = "SELECT * FROM epic e WHERE e.product_id = :productId", nativeQuery = true)
    Optional<List<Epic>> findAllEpicsByProductId(Long productId);
}
