package mil.af.abms.midas.api.team;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.team.dto.TeamDTO;

public interface TeamRepository extends RepositoryInterface<TeamEntity, TeamDTO> {
    Optional<TeamEntity> findByName(String name);
}
