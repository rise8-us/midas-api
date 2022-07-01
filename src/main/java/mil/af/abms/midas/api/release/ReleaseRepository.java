package mil.af.abms.midas.api.release;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.release.dto.ReleaseDTO;

public interface ReleaseRepository extends RepositoryInterface<Release, ReleaseDTO> {
    Optional<Release> findByUid(String uId);

    @Query(value = "SELECT * FROM releases r WHERE r.project_id = :projectId", nativeQuery = true)
    Optional<List<Release>> findAllReleasesByProjectId(Long projectId);

    @Query(value = "SELECT * FROM releases r WHERE r.released_at < :currentReleasedAt AND r.project_id = :projectId ORDER BY released_at DESC LIMIT 1", nativeQuery = true)
    Optional<Release> findPreviousReleaseByProjectIdAndReleasedAt(Long projectId, LocalDateTime currentReleasedAt);
}
