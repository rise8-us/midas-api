package mil.af.abms.midas.api.application;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.application.dto.ApplicationDTO;

public interface ApplicationRepository extends RepositoryInterface<Application, ApplicationDTO> {
    Optional<Application> findByName(String name);
}
