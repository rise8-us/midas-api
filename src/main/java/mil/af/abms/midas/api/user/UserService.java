package mil.af.abms.midas.api.user;

import javax.transaction.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.dto.UpdateUserDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserDisabledDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserRolesDTO;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.config.CustomProperty;
import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationToken;
import mil.af.abms.midas.enums.Roles;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class UserService extends AbstractCRUDService<User, UserDTO, UserRepository> {

    private final CustomProperty property;
    private TeamService teamService;

    @Autowired
    public UserService(UserRepository repository, CustomProperty property, TeamService teamService) {
        super(repository, User.class, UserDTO.class);
        this.property = property;
        this.teamService = teamService;
    }

    @Transactional
    public User create(PlatformOneAuthenticationToken token) {
        Boolean isAdmin = token.getGroups().stream().anyMatch(g -> g.contains(property.getJwtAdminGroup()));
        Long rolesAsLong = Roles.setRoles(0L, Map.of(Roles.ADMIN, isAdmin));
        User user = Builder.build(User.class)
                .with(u -> u.setKeycloakUid(token.getKeycloakUid()))
                .with(u -> u.setDodId(token.getDodId()))
                .with(u -> u.setDisplayName(token.getDisplayName()))
                .with(u -> u.setRoles(rolesAsLong))
                .with(u -> u.setEmail(token.getEmail())).get();
        return repository.save(user);
    }

    @Transactional
    public User updateById(Long id, UpdateUserDTO updateUserDTO) {
        User user = getObject(id);

        if (updateUserDTO.getTeamIds().size() > 0) {
            Set<Team> teams = updateUserDTO.getTeamIds().stream().map(teamService::getObject).collect(Collectors.toSet());
            user.setTeams(teams);
        } else {
            user.setTeams(null);
        }

        user.setUsername(updateUserDTO.getUsername());
        user.setEmail(updateUserDTO.getEmail());
        user.setDisplayName(updateUserDTO.getDisplayName());

        return repository.save(user);
    }

    @Transactional
    public User updateRolesById(Long id, UpdateUserRolesDTO updateUserRolesDTO) {
        User user = getObject(id);
        user.setRoles(updateUserRolesDTO.getRoles());

        return repository.save(user);
    }

    @Transactional
    public User updateIsDisabledById(Long id, UpdateUserDisabledDTO updateUserDisabledDTO) {
        User user = getObject(id);

        user.setIsDisabled(updateUserDisabledDTO.isDisabled());

        return repository.save(user);
    }

    @Transactional
    public User findByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(User.class.getSimpleName(), "username", username));
    }

    @Transactional
    public Optional<User> findByKeycloakUid(String keycloakUid) {
        return repository.findByKeycloakUid(keycloakUid);
    }

    @Transactional
    public User getUserFromAuth(Authentication auth) {
        String keycloakUid = JsonMapper.getKeycloakUidFromAuth(auth);

        return findByKeycloakUid(keycloakUid).orElseThrow(() ->
                new EntityNotFoundException(
                        User.class.getSimpleName(),
                        "keycloakUid",
                        String.valueOf(keycloakUid)
                ));
    }

}
