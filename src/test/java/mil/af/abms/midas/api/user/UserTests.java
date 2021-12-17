package mil.af.abms.midas.api.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.enums.UserType;

public class UserTests {

    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("midas")).get();

    private final User user = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("grogu"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("baby yoda"))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setTeams(Set.of(team)))
            .with(u -> u.setRoles(0L))
            .with(u -> u.setUserType(UserType.ACTIVE))
            .with(u -> u.setIsDisabled(false)).get();
    private final UserDTO userDTO = Builder.build(UserDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setKeycloakUid("abc-123"))
            .with(d -> d.setUsername("grogu"))
            .with(d -> d.setEmail("a.b@c"))
            .with(d -> d.setDisplayName("baby yoda"))
            .with(d -> d.setCreationDate(user.getCreationDate()))
            .with(d -> d.setDodId(1L))
            .with(d -> d.setTeamIds(Set.of(1L)))
            .with(d -> d.setRoles(0L))
            .with(d -> d.setUserType(UserType.ACTIVE))
            .with(d -> d.setIsDisabled(false)).get();

    @Test
    public void should_have_all_userDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(User.class, fields::add);

        assertThat(fields.size()).isEqualTo(UserDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_set_and_get_properties() {

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getKeycloakUid()).isEqualTo("abc-123");
        assertThat(user.getUsername()).isEqualTo("grogu");
        assertThat(user.getEmail()).isEqualTo("a.b@c");
        assertThat(user.getDisplayName()).isEqualTo("baby yoda");
        assertThat(user.getDodId()).isEqualTo(1);
        assertThat(user.getRoles()).isEqualTo(0L);
        assertThat(user.getIsDisabled()).isEqualTo(false);
        assertThat(user.getUserType()).isEqualTo(UserType.ACTIVE);
    }

    @Test
    public void should_return_dto() {
        assertThat(user.toDto()).isEqualTo(userDTO);
    }

    @Test
    public void should_be_equal() {
        User user2 = new User();
        BeanUtils.copyProperties(user, user2);

        assertEquals(user, user);
        assertNotEquals(null, user);
        assertNotEquals(user, new Team());
        assertEquals(user, user2);
        assertNotEquals(user, new User());
    }

}
