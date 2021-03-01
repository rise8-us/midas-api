package mil.af.abms.midas.api.team;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;

@RestController
@RequestMapping("/api/teams")
public class TeamController extends AbstractCRUDController<TeamEntity, TeamDTO, TeamService> {

    @Autowired
    private TeamService service;
    public TeamController(TeamService service) {
        super(service);
    }

    @PostMapping
    public TeamDTO create(@Valid @RequestBody CreateTeamDTO teamDTO) {
        return service.create(teamDTO).toDto();
    }

    @PutMapping("/{id}")
    public TeamDTO updateById(@Valid @RequestBody UpdateTeamDTO updateTeamDTO, @PathVariable Long id) {
        return service.updateById(id, updateTeamDTO).toDto();
    }
}
