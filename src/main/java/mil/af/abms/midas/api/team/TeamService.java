package mil.af.abms.midas.api.team;

import javax.transaction.Transactional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamIsArchivedDTO;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class TeamService extends AbstractCRUDService<Team, TeamDTO, TeamRepository> {

    private UserService userService;

    public TeamService(TeamRepository repository) {
        super(repository, Team.class, TeamDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Transactional
    public Team create(CreateTeamDTO createTeamDTO) {
        Team newTeam = Builder.build(Team.class)
                .with(t -> t.setName(createTeamDTO.getName()))
                .with(t -> t.setDescription(createTeamDTO.getDescription()))
                .with(t -> t.setGitlabGroupId(createTeamDTO.getGitlabGroupId()))
                .with(t -> t.setUsers(
                        createTeamDTO.getUserIds().stream().map(userService::findById).collect(Collectors.toSet()))
                ).get();

        return repository.save(newTeam);
    }

    @Transactional
    public Team findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Team.class.getSimpleName(), "name", name));
    }

    @Transactional
    public Team updateById(Long id, UpdateTeamDTO updateTeamDTO) {
        Team foundTeam = findById(id);
        foundTeam.setName(updateTeamDTO.getName());
        foundTeam.setGitlabGroupId(updateTeamDTO.getGitlabGroupId());
        foundTeam.setDescription(updateTeamDTO.getDescription());
        foundTeam.setUsers(updateTeamDTO.getUserIds().stream().map(userService::findById).collect(Collectors.toSet()));

        return repository.save(foundTeam);
    }

    @Transactional
    public Team updateIsArchivedById(Long id, UpdateTeamIsArchivedDTO updateTeamIsArchivedDTO) {
        Team team = findById(id);

        team.setIsArchived(updateTeamIsArchivedDTO.getIsArchived());

        return repository.save(team);
    }
}
