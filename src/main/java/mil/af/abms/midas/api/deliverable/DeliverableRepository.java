package mil.af.abms.midas.api.deliverable;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.deliverable.dto.DeliverableDTO;

public interface DeliverableRepository extends RepositoryInterface<Deliverable, DeliverableDTO> {
    @Query(value = "SELECT * FROM deliverable d WHERE d.epic_id = :epicId", nativeQuery = true)
    Optional<List<Deliverable>> findAllDeliverablesByEpicId(Long epicId);
}
