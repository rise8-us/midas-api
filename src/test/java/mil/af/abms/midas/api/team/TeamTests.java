package mil.af.abms.midas.api.team;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.user.User;

public class TeamTests {

    private static final LocalDateTime TEST_TIME = LocalDateTime.now();

    private Set<User> users = Set.of(Builder.build(User.class).with(u -> u.setId(3L)).get());
    private Set<Product> products = Set.of(Builder.build(Product.class).with(u -> u.setId(3L)).get());
    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setIsArchived(false))
            .with(t -> t.setCreationDate(TEST_TIME))
            .with(t -> t.setProducts(products))
            .with(t -> t.setDescription("dev team"))
            .with(t -> t.setUsers(users)).get();
    private final TeamDTO teamDTOExpected = Builder.build(TeamDTO.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setIsArchived(false))
            .with(t -> t.setDescription("dev team"))
            .with(t -> t.setCreationDate(TEST_TIME)).get();

    @Test
    public void should_be_equal() {
        Team team2 = new Team();
        BeanUtils.copyProperties(team, team2);

        assertTrue(team.equals(team));
        assertFalse(team.equals(null));
        assertFalse(team.equals(new User()));
        assertFalse(team.equals(new Team()));
        assertTrue(team.equals(team2));
    }

    @Test
    public void should_get_properties() {
        assertThat(team.getId()).isEqualTo(1L);
        assertThat(team.getName()).isEqualTo("MIDAS");
        assertFalse(team.getIsArchived());
        assertThat(team.getCreationDate()).isEqualTo(TEST_TIME);
        assertThat(team.getDescription()).isEqualTo("dev team");
        assertTrue(team.getProducts().equals(products));
        assertTrue(team.getUsers().equals(users));
    }

    @Test
    public void can_return_dto() {
        assertThat(team.toDto()).isEqualTo(teamDTOExpected);
    }
}
