package mil.af.abms.midas.api.personnel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.personnel.dto.UpdatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(PersonnelService.class)
public class PersonnelServiceTests {

    @SpyBean
    PersonnelService personnelService;
    @MockBean
    SimpMessageSendingOperations websocket;
    @MockBean
    UserService userService;
    @MockBean
    ProductService productService;
    @MockBean
    PortfolioService portfolioService;
    @MockBean
    TeamService teamService;
    @MockBean
    PersonnelRepository personnelRepository;
    @Captor
    ArgumentCaptor<Personnel> personnelCaptor;

    private final User lambo = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("Lambo"))
            .get();
    private final User danny = Builder.build(User.class)
            .with(u -> u.setId(2L))
            .with(u -> u.setKeycloakUid("bcd-234"))
            .with(u -> u.setUsername("Danny"))
            .get();
    private final User paul = Builder.build(User.class)
            .with(u -> u.setId(3L))
            .with(u -> u.setKeycloakUid("bcd-234"))
            .with(u -> u.setUsername("Danny"))
            .get();
    private final User dustin = Builder.build(User.class)
            .with(u -> u.setId(4L))
            .with(u -> u.setKeycloakUid("bcd-234"))
            .with(u -> u.setUsername("Danny"))
            .get();
    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(5L))
            .with(t -> t.setName("Team"))
            .get();
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(6L))
            .with(p -> p.setGitlabGroupId(420))
            .with(p -> p.setName("portfolio"))
            .get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(7L))
            .with(p -> p.setGitlabGroupId(123))
            .with(p -> p.setName("product"))
            .get();
    private final Personnel personnel = Builder.build(Personnel.class)
            .with(p -> p.setId(8L))
            .with(p -> p.setOwner(lambo))
            .with(p -> p.setAdmins(Set.of(danny)))
            .with(p -> p.setTeams(Set.of(team)))
            .get();

    @Test
    void should_create_personnel() {
        CreatePersonnelDTO createPersonnelDTO = Builder.build(CreatePersonnelDTO.class)
                .with(p -> p.setOwnerId(1L))
                .with(p -> p.setAdminIds(Set.of(2L)))
                .with(p -> p.setTeamIds(Set.of(5L)))
                .get();

        when(userService.findByIdOrNull(1L)).thenReturn(lambo);
        when(userService.findByIdOrNull(2L)).thenReturn(danny);
        when(teamService.findByIdOrNull(5L)).thenReturn(team);
        when(portfolioService.findByIdOrNull(6L)).thenReturn(portfolio);
        when(productService.findByIdOrNull(7L)).thenReturn(product);
        when(personnelRepository.save(any())).thenReturn(personnel);

        personnelService.create(createPersonnelDTO);

        verify(personnelRepository, times(1)).save(personnelCaptor.capture());
        Personnel personnelSaved = personnelCaptor.getValue();

        assertThat(personnelSaved.getOwner()).isEqualTo(lambo);
        assertThat(personnelSaved.getAdmins()).isEqualTo(Set.of(danny));
        assertThat(personnelSaved.getTeams()).isEqualTo(Set.of(team));
    }

    @Test
    void should_find_by_id() {
        when(personnelRepository.findById(8L)).thenReturn(Optional.of(personnel));

        assertThat(personnelService.findById(8L)).isEqualTo(personnel);
    }

    @Test
    void should_throw_error_find_by_id() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () -> personnelService.findById(111L));
    }

    @Test
    void should_update_personnel_by_id() {
        UpdatePersonnelDTO updatePersonnelDTO = Builder.build(UpdatePersonnelDTO.class)
                .with(p -> p.setOwnerId(3L))
                .with(p -> p.setAdminIds(Set.of(4L)))
                .with(p -> p.setTeamIds(Set.of(5L)))
                .get();

        when(userService.findByIdOrNull(3L)).thenReturn(paul);
        when(userService.findByIdOrNull(4L)).thenReturn(dustin);
        when(teamService.findByIdOrNull(5L)).thenReturn(team);
        when(portfolioService.findByIdOrNull(6L)).thenReturn(portfolio);
        when(productService.findByIdOrNull(7L)).thenReturn(product);
        when(personnelRepository.findById(8L)).thenReturn(Optional.of(personnel));
        when(personnelRepository.save(personnel)).thenReturn(personnel);


        personnelService.updateById(8L, updatePersonnelDTO);

        verify(personnelRepository, times(1)).save(personnelCaptor.capture());
        Personnel personnelSaved = personnelCaptor.getValue();

        assertThat(personnelSaved.getOwner()).isEqualTo(paul);
        assertThat(personnelSaved.getAdmins()).isEqualTo(Set.of(dustin));
        assertThat(personnelSaved.getTeams()).isEqualTo(Set.of(team));
    }
}
