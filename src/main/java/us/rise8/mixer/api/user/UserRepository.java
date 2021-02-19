package us.rise8.mixer.api.user;

import java.util.Optional;

import us.rise8.mixer.api.RepositoryInterface;
import us.rise8.mixer.api.user.dto.UserDTO;

public interface UserRepository extends RepositoryInterface<UserModel, UserDTO> {

    Optional<UserModel> findByUsername(String username);

    Optional<UserModel> findByDodId(Long dodId);

    Optional<UserModel> findByKeycloakUid(String keycloakId);
}
