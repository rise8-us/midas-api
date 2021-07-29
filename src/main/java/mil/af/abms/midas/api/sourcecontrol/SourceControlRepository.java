package mil.af.abms.midas.api.sourcecontrol;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.sourcecontrol.dto.SourceControlDTO;

public interface SourceControlRepository extends RepositoryInterface<SourceControl, SourceControlDTO> {

    public Optional<SourceControl> findByName(String name);
}
