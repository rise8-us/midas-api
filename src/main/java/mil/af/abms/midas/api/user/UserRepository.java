package mil.af.abms.midas.api.user;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.user.dto.UserDTO;

public interface UserRepository extends RepositoryInterface<UserModel, UserDTO> {

    Optional<UserModel> findByUsername(String username);

    Optional<UserModel> findByDodId(Long dodId);

    Optional<UserModel> findByKeycloakUid(String keycloakId);
}
