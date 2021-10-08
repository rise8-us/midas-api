package mil.af.abms.midas.api.team;

import javax.transaction.Transactional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.team.dto.CreateTeamDTO;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamDTO;
import mil.af.abms.midas.api.team.dto.UpdateTeamIsArchivedDTO;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class TeamService extends AbstractCRUDService<Team, TeamDTO, TeamRepository> {

    private UserService userService;
    private ProductService productService;
    private final SimpMessageSendingOperations websocket;

    public TeamService(TeamRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Team.class, TeamDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }
    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }

    @Transactional
    public Team create(CreateTeamDTO dto) {
        Team newTeam = Builder.build(Team.class)
                .with(t -> t.setName(dto.getName()))
                .with(t -> t.setDescription(dto.getDescription()))
                .with(t -> t.setGitlabGroupId(dto.getGitlabGroupId()))
                .with(t -> t.setProductManager(userService.findByIdOrNull(dto.getProductManagerId())))
                .with(t -> t.setDesigner(userService.findByIdOrNull(dto.getDesignerId())))
                .with(t -> t.setTechLead(userService.findByIdOrNull(dto.getTechLeadId())))
                .with(t -> t.setProducts(dto.getProductIds().stream().map(productService::findById)
                        .collect(Collectors.toSet())))
                .with(t -> t.setMembers(
                        dto.getUserIds().stream().map(userService::findById).collect(Collectors.toSet()))
                ).get();

        var savedTeam = repository.save(newTeam);

        newTeam.getProducts().forEach(product -> {
            product.getTeams().add(savedTeam);
            websocket.convertAndSend("/topic/update_product", product.toDto());
        });

        return savedTeam;
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
        foundTeam.setGitlabGroupId(dto.getGitlabGroupId());
        foundTeam.setDescription(dto.getDescription());
        foundTeam.setProductManager(userService.findByIdOrNull(dto.getProductManagerId()));
        foundTeam.setDesigner(userService.findByIdOrNull(dto.getDesignerId()));
        foundTeam.setTechLead(userService.findByIdOrNull(dto.getTechLeadId()));
        foundTeam.setMembers(dto.getUserIds().stream().map(userService::findById).collect(Collectors.toSet()));

        return repository.save(foundTeam);
    }

    @Transactional
    public Team updateIsArchivedById(Long id, UpdateTeamIsArchivedDTO updateTeamIsArchivedDTO) {
        Team team = findById(id);

        team.setIsArchived(updateTeamIsArchivedDTO.getIsArchived());

        return repository.save(team);
    }
}
