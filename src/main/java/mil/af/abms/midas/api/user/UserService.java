package mil.af.abms.midas.api.user;

import javax.transaction.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public UserService(UserRepository repository, CustomProperty property) {
        super(repository, User.class, UserDTO.class);
        this.property = property;
    }

    @Autowired
    public void setTeamService(TeamService teamService) { this.teamService = teamService; }

    @Transactional
    public User create(PlatformOneAuthenticationToken token) {
        Boolean isAdmin = token.getGroups().stream().anyMatch(g -> g.contains(property.getJwtAdminGroup()));
        Long rolesAsLong = Roles.setRoles(0L, Map.of(Roles.ADMIN, isAdmin));
        User user = Builder.build(User.class)
                .with(u -> u.setKeycloakUid(token.getKeycloakUid()))
                .with(u -> u.setDodId(token.getDodId()))
                .with(u -> u.setDisplayName(token.getDisplayName()))
                .with(u -> u.setUsername(token.getDisplayName()))
                .with(u -> u.setRoles(rolesAsLong))
                .with(u -> u.setEmail(token.getEmail())).get();
        return repository.save(user);
    }

    @Transactional
    public User getUserBySecContext() {
        String keycloakUid = JsonMapper.getKeycloakUidFromAuth(SecurityContextHolder.getContext().getAuthentication());
        return getByKeycloakUid(keycloakUid);
    }

    @Transactional
    public User updateById(Long id, UpdateUserDTO dto) {
        User user = findById(id);

        Set<Team> teams = dto.getTeamIds().stream().map(teamService::findById).collect(Collectors.toSet());
        user.setTeams(teams);

        user.setUsername(Optional.ofNullable(dto.getUsername()).orElse(user.getUsername()));
        user.setEmail(Optional.ofNullable(dto.getEmail()).orElse(user.getEmail()));
        user.setDisplayName(Optional.ofNullable(dto.getDisplayName()).orElse(user.getDisplayName()));
        user.setPhone(Optional.ofNullable(dto.getPhone()).orElse(user.getPhone()));
        user.setCompany(Optional.ofNullable(dto.getCompany()).orElse(user.getCompany()));

        return repository.save(user);
    }

    @Transactional
    public User updateRolesById(Long id, UpdateUserRolesDTO updateUserRolesDTO) {
        User user = findById(id);
        user.setRoles(updateUserRolesDTO.getRoles());

        return repository.save(user);
    }

    @Transactional
    public User updateIsDisabledById(Long id, UpdateUserDisabledDTO updateUserDisabledDTO) {
        User user = findById(id);

        user.setIsDisabled(updateUserDisabledDTO.isDisabled());

        return repository.save(user);
    }

    @Transactional
    public User findByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(User.class.getSimpleName(), "username", username)
        );
    }

    @Transactional
    public Optional<User> findByKeycloakUid(String keycloakUid) {
        return repository.findByKeycloakUid(keycloakUid);
    }

    @Transactional
    public User getByKeycloakUid(String keycloakUid) {
        return repository.findByKeycloakUid(keycloakUid).orElseThrow(
                () -> new EntityNotFoundException(User.class.getSimpleName(), "keycloakUid", String.valueOf(keycloakUid))
        );
    }

}
