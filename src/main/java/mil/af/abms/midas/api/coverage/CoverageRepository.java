package mil.af.abms.midas.api.coverage;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.coverage.dto.CoverageDTO;

public interface CoverageRepository extends RepositoryInterface<Coverage, CoverageDTO> {

    @Query(value = "SELECT * FROM coverage where project_id = :projectId ORDER BY id DESC LIMIT 1", nativeQuery = true)
    List<Coverage> findCurrentForProject(@Param("projectId") Long projectId);
}
