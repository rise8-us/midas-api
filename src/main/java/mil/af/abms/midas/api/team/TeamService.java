package mil.af.abms.midas.api.team;

import javax.transaction.Transactional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;
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
    public Team create(CreateTeamDTO dto) {
        Team newTeam = Builder.build(Team.class)
                .with(t -> t.setName(dto.getName()))
                .with(t -> t.setDescription(dto.getDescription()))
                .with(t -> t.setProductManager(userService.findByIdOrNull(dto.getProductManagerId())))
                .with(t -> t.setDesigner(userService.findByIdOrNull(dto.getDesignerId())))
                .with(t -> t.setTechLead(userService.findByIdOrNull(dto.getTechLeadId())))
                .with(t -> t.setMembers(
                        dto.getUserIds().stream().map(userService::findById).collect(Collectors.toSet()))
                ).get();

        return repository.save(newTeam);
    }

    @Transactional
    public Team findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Team.class.getSimpleName(), "name", name));
    }

    @Transactional
    public Team updateById(Long id, UpdateTeamDTO dto) {
        Team foundTeam = findById(id);
        foundTeam.setName(dto.getName());
        foundTeam.setDescription(dto.getDescription());
        foundTeam.setProductManager(userService.findByIdOrNull(dto.getProductManagerId()));
        foundTeam.setDesigner(userService.findByIdOrNull(dto.getDesignerId()));
        foundTeam.setTechLead(userService.findByIdOrNull(dto.getTechLeadId()));
        foundTeam.setMembers(dto.getUserIds().stream().map(userService::findById).collect(Collectors.toSet()));

        return repository.save(foundTeam);
    }

    @Transactional
    public Team updateIsArchivedById(Long id, IsArchivedDTO isArchivedDTO) {
        Team team = findById(id);

        team.setIsArchived(isArchivedDTO.getIsArchived());

        return repository.save(team);
    }
}
