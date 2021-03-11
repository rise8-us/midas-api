package mil.af.abms.midas.api.user;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.user.dto.UserDTO;

public interface UserRepository extends RepositoryInterface<User, UserDTO> {

    Optional<User> findByUsername(String username);

    Optional<User> findByKeycloakUid(String keycloakId);
}
