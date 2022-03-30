package mil.af.abms.midas.api.personnel;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;
import mil.af.abms.midas.api.personnel.dto.PersonnelInterfaceDTO;
import mil.af.abms.midas.api.personnel.dto.UpdatePersonnelDTO;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.UserService;

@Service
public class PersonnelService extends AbstractCRUDService<Personnel, PersonnelDTO, PersonnelRepository> {

    private UserService userService;
    private TeamService teamService;

    public PersonnelService(PersonnelRepository repository) {
        super(repository, Personnel.class, PersonnelDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }
    @Autowired
    public void setTeamService(TeamService teamService) { this.teamService = teamService; }

    @Transactional
    public Personnel create(CreatePersonnelDTO dto) {
        Personnel newPersonnel = new Personnel();
        updateRequiredNotNullFields(dto, newPersonnel);

        return repository.save(newPersonnel);
    }

    protected void updateRequiredNotNullFields(PersonnelInterfaceDTO dto, Personnel foundPersonnel) {
        foundPersonnel.setOwner(userService.findByIdOrNull(dto.getOwnerId()));
        Optional.ofNullable(dto.getTeamIds()).ifPresent((teamIds -> {
            var teams = teamIds.stream().map(teamService::findByIdOrNull).collect(Collectors.toSet());
            foundPersonnel.setTeams(teams);
        }));
        Optional.ofNullable(dto.getAdminIds()).ifPresent((adminIds -> {
            var admins = adminIds.stream().map(userService::findByIdOrNull).collect(Collectors.toSet());
            foundPersonnel.setAdmins(admins);
        }));
    }

    public Personnel updateById(Long id, UpdatePersonnelDTO dto) {
        Personnel foundPersonnel = findById(id);
        updateRequiredNotNullFields(dto, foundPersonnel);

        return repository.save(foundPersonnel);
    }

}
