package mil.af.abms.midas.api.epic;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.epic.dto.EpicDTO;

public interface EpicRepository extends RepositoryInterface<Epic, EpicDTO> {
    Optional<Epic> findByEpicUid(Long uId);
}
