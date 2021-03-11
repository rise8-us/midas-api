package mil.af.abms.midas.api.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class TeamService extends AbstractCRUDService<TeamEntity, TeamDTO, TeamRepository> {

    @Autowired
    public TeamService(TeamRepository repository) {
        super(repository, TeamEntity.class, TeamDTO.class);
    }

    public TeamEntity create(CreateTeamDTO createTeamDTO) {
        TeamEntity newTeam = Builder.build(TeamEntity.class)
                .with(t -> t.setName(createTeamDTO.getName()))
                .with(t -> t.setDescription(createTeamDTO.getDescription()))
                .with(t -> t.setGitlabGroupId(createTeamDTO.getGitlabGroupId())).get();

        return repository.save(newTeam);
    }

    public TeamEntity findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(TeamEntity.class.getSimpleName(), "name", name));
    }

    public TeamEntity updateById(Long id, UpdateTeamDTO updateTeamDTO) {
        TeamEntity foundTeam = getObject(id);
        foundTeam.setName(updateTeamDTO.getName());
        foundTeam.setGitlabGroupId(updateTeamDTO.getGitlabGroupId());
        foundTeam.setIsArchived(updateTeamDTO.getIsArchived());
        foundTeam.setDescription(updateTeamDTO.getDescription());

        return repository.save(foundTeam);
    }
}
