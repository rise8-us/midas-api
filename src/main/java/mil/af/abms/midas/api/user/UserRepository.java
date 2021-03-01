package mil.af.abms.midas.api.user;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.user.dto.UserDTO;

public interface UserRepository extends RepositoryInterface<UserEntity, UserDTO> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByDodId(Long dodId);

    Optional<UserEntity> findByKeycloakUid(String keycloakId);
}
