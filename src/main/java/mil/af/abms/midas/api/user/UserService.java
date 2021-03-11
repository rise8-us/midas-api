package mil.af.abms.midas.api.user;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.api.user.dto.UpdateUserDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserDisabledDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserRolesDTO;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationToken;
import mil.af.abms.midas.enums.Roles;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class UserService extends AbstractCRUDService<UserEntity, UserDTO, UserRepository> {

    @Autowired
    public UserService(UserRepository repository) {
        super(repository, UserEntity.class, UserDTO.class);
    }

    public UserEntity create(PlatformOneAuthenticationToken token) {
        Boolean isAdmin = token.getGroups().stream().anyMatch(g -> g.contains("midas-IL2-admin"));  //add group name in application.yml
        Long rolesAsLong = Roles.setRoles(0L, Map.of(Roles.ADMIN, isAdmin));
        UserEntity user = Builder.build(UserEntity.class)
                .with(u -> u.setKeycloakUid(token.getKeycloakUid()))
                .with(u -> u.setDodId(token.getDodId()))
                .with(u -> u.setDisplayName(token.getDisplayName()))
                .with(u -> u.setRoles(rolesAsLong))
                .with(u -> u.setEmail(token.getEmail())).get();
        return repository.save(user);
    }

    public UserEntity updateById(Long id, UpdateUserDTO updateUserDTO) {
        UserEntity user = getObject(id);
        user.setUsername(updateUserDTO.getUsername());
        user.setEmail(updateUserDTO.getEmail());
        user.setDisplayName(updateUserDTO.getDisplayName());

        return repository.save(user);
    }

    public UserEntity updateRolesById(Long id, UpdateUserRolesDTO updateUserRolesDTO) {
        UserEntity user = getObject(id);
        user.setRoles(updateUserRolesDTO.getRoles());

        return repository.save(user);
    }

    public UserEntity updateIsDisabledById(Long id, UpdateUserDisabledDTO updateUserDisabledDTO) {
        UserEntity user = getObject(id);

        user.setIsDisabled(updateUserDisabledDTO.isDisabled());

        return repository.save(user);
    }

    public UserEntity findByUsername(String username) {
        UserEntity user = repository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(UserEntity.class.getSimpleName(), "username", username));
        return user;
    }

    public Optional<UserEntity> findByKeycloakUid(String keycloakUid) {
        return repository.findByKeycloakUid(keycloakUid);
    }

    public UserEntity getUserFromAuth(Authentication auth) {
        String keycloakUid = JsonMapper.getKeycloakUidFromAuth(auth);

        return findByKeycloakUid(keycloakUid).orElseThrow(() ->
                new EntityNotFoundException(
                        UserEntity.class.getSimpleName(),
                        "keycloakUid",
                        String.valueOf(keycloakUid)
                ));
    }

}
