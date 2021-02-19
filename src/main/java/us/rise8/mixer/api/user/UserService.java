package us.rise8.mixer.api.user;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import us.rise8.mixer.api.AbstractCRUDService;
import us.rise8.mixer.api.helper.Builder;
import us.rise8.mixer.api.helper.JsonMapper;
import us.rise8.mixer.api.user.dto.UpdateUserDTO;
import us.rise8.mixer.api.user.dto.UpdateUserDisabledDTO;
import us.rise8.mixer.api.user.dto.UpdateUserRolesDTO;
import us.rise8.mixer.api.user.dto.UserDTO;
import us.rise8.mixer.config.auth.platform1.PlatformOneAuthenticationToken;
import us.rise8.mixer.enums.Roles;
import us.rise8.mixer.exception.EntityNotFoundException;

@Service
public class UserService extends AbstractCRUDService<UserModel, UserDTO, UserRepository> {

    @Autowired
    public UserService(UserRepository repository) {
        super(repository, UserModel.class, UserDTO.class);
    }

    public UserModel create(PlatformOneAuthenticationToken token) {
        Boolean isAdmin = token.getGroups().stream().anyMatch(g -> g.contains("mixer-IL2-admin"));  //add group name in application.yml
        Long rolesAsLong = Roles.setRoles(0L, Map.of(Roles.ADMIN, isAdmin));
        UserModel user = Builder.build(UserModel.class)
                .with(u -> u.setKeycloakUid(token.getKeycloakUid()))
                .with(u -> u.setDodId(token.getDodId()))
                .with(u -> u.setDisplayName(token.getDisplayName()))
                .with(u -> u.setRoles(rolesAsLong))
                .with(u -> u.setEmail(token.getEmail())).get();
        return repository.save(user);
    }

    public UserDTO updateById(Long id, UpdateUserDTO updateUserDTO) {
        UserModel user = getObject(id);
        user.setUsername(updateUserDTO.getUsername());
        user.setEmail(updateUserDTO.getEmail());
        user.setDisplayName(updateUserDTO.getDisplayName());

        return repository.save(user).toDto();
    }

    public UserDTO updateRolesById(Long id, UpdateUserRolesDTO updateUserRolesDTO) {
        UserModel user = getObject(id);
        user.setRoles(updateUserRolesDTO.getRoles());

        return repository.save(user).toDto();
    }

    public UserDTO updateIsDisabledById(Long id, UpdateUserDisabledDTO updateUserDisabledDTO) {
        UserModel user = getObject(id);

        user.setIsDisabled(updateUserDisabledDTO.isDisabled());

        return repository.save(user).toDto();
    }

    public UserDTO findByUsername(String username) {
        UserModel user = repository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(UserModel.class.getSimpleName(), "username", username));
        return user.toDto();
    }

    public Optional<UserModel> findByKeycloakUid(String keycloakUid) {
        return repository.findByKeycloakUid(keycloakUid);
    }

    public UserModel getUserFromAuth(Authentication auth) {
        String keycloakUid = JsonMapper.getKeycloakUidFromAuth(auth);

        return findByKeycloakUid(keycloakUid).orElseThrow(() ->
                new EntityNotFoundException(
                        UserModel.class.getSimpleName(),
                        "keycloakUid",
                        String.valueOf(keycloakUid)
                ));
    }
}
