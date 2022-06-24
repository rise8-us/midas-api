package mil.af.abms.midas.api.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.user.User;

class TeamTests {

    private final Set<User> users = Set.of(Builder.build(User.class).with(u -> u.setId(3L)).get());
    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setIsArchived(false))
            .with(t -> t.setDescription("dev team"))
            .with(t -> t.setMembers(users)).get();
    private final TeamDTO teamDTOExpected = Builder.build(TeamDTO.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setIsArchived(false))
            .with(t -> t.setDescription("dev team"))
            .with(t -> t.setCreationDate(team.getCreationDate()))
            .with(t -> t.setUserIds(Set.of(3L)))
            .with(t -> t.setPersonnelIds(Set.of()))
            .get();

    @Test
    void should_have_all_teamDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Team.class, fields::add);

        assertThat(fields).hasSize(TeamDTO.class.getDeclaredFields().length + 1);
    }

    @Test
    void should_be_equal() {
        Team team2 = new Team();
        BeanUtils.copyProperties(team, team2);

        assertEquals(team, team);
        assertNotEquals(null, team);
        assertNotEquals(team, new User());
        assertNotEquals(team, new Team());
        assertEquals(team, team2);
    }

    @Test
    void should_get_properties() {
        assertThat(team.getId()).isEqualTo(1L);
        assertThat(team.getName()).isEqualTo("MIDAS");
        assertFalse(team.getIsArchived());
        assertThat(team.getDescription()).isEqualTo("dev team");
        assertEquals(team.getMembers(), users);
    }

    @Test
    void can_return_dto() {
        assertThat(team.toDto()).isEqualTo(teamDTOExpected);
    }
}
