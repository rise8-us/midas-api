package mil.af.abms.midas.api.team;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.user.User;

class TeamTests {

    private static final LocalDateTime TEST_TIME = LocalDateTime.now();

    private final Set<User> users = Set.of(Builder.build(User.class).with(u -> u.setId(3L)).get());
    private final Set<Project> projects = Set.of(Builder.build(Project.class).with(u -> u.setId(3L)).get());
    private final Set<Product> products = Set.of(Builder.build(Product.class).with(u -> u.setId(4L)).get());
    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setIsArchived(false))
            .with(t -> t.setCreationDate(TEST_TIME))
            .with(t -> t.setProjects(projects))
            .with(t -> t.setDescription("dev team"))
            .with(t -> t.setProducts(products))
            .with(t -> t.setMembers(users)).get();
    private final TeamDTO teamDTOExpected = Builder.build(TeamDTO.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setIsArchived(false))
            .with(t -> t.setDescription("dev team"))
            .with(t -> t.setCreationDate(TEST_TIME))
            .with(t -> t.setUserIds(Set.of(3L)))
            .with(t -> t.setProductIds(Set.of(4L)))
            .with(t -> t.setProjectIds(Set.of(3L))).get();

    @Test
    void should_have_all_teamDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Team.class, fields::add);

        assertThat(fields.size()).isEqualTo(TeamDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Team team2 = new Team();
        BeanUtils.copyProperties(team, team2);

        assertTrue(team.equals(team));
        assertFalse(team.equals(null));
        assertFalse(team.equals(new User()));
        assertFalse(team.equals(new Team()));
        assertTrue(team.equals(team2));
    }

    @Test
    void should_get_properties() {
        assertThat(team.getId()).isEqualTo(1L);
        assertThat(team.getName()).isEqualTo("MIDAS");
        assertFalse(team.getIsArchived());
        assertThat(team.getCreationDate()).isEqualTo(TEST_TIME);
        assertThat(team.getDescription()).isEqualTo("dev team");
        assertTrue(team.getProjects().equals(projects));
        assertTrue(team.getMembers().equals(users));
    }

    @Test
    void can_return_dto() {
        assertThat(team.toDto()).isEqualTo(teamDTOExpected);
    }
}
