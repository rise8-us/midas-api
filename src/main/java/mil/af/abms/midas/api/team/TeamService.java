package mil.af.abms.midas.api.team;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class TeamService extends AbstractCRUDService<Team, TeamDTO, TeamRepository> {

    @Autowired
    public TeamService(TeamRepository repository) {
        super(repository, Team.class, TeamDTO.class);
    }

    @Transactional
    public Team create(CreateTeamDTO createTeamDTO) {
        Team newTeam = Builder.build(Team.class)
                .with(t -> t.setName(createTeamDTO.getName()))
                .with(t -> t.setDescription(createTeamDTO.getDescription()))
                .with(t -> t.setGitlabGroupId(createTeamDTO.getGitlabGroupId())).get();

        return repository.save(newTeam);
    }

    @Transactional
    public Team findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Team.class.getSimpleName(), "name", name));
    }

    @Transactional
    public Team updateById(Long id, UpdateTeamDTO updateTeamDTO) {
        Team foundTeam = getObject(id);
        foundTeam.setName(updateTeamDTO.getName());
        foundTeam.setGitlabGroupId(updateTeamDTO.getGitlabGroupId());
        foundTeam.setIsArchived(updateTeamDTO.getIsArchived());
        foundTeam.setDescription(updateTeamDTO.getDescription());

        return repository.save(foundTeam);
    }
}
