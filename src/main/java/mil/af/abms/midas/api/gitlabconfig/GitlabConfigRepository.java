package mil.af.abms.midas.api.gitlabconfig;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.gitlabconfig.dto.GitlabConfigDTO;

public interface GitlabConfigRepository extends RepositoryInterface<GitlabConfig, GitlabConfigDTO> {

    public Optional<GitlabConfig> findByName(String name);
}
