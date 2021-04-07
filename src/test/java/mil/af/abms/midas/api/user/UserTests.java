package mil.af.abms.midas.api.user;

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
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.dto.UserDTO;

public class UserTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("midas")).get();
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(3L)).get();

    private final User expectedUser = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("grogu"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("baby yoda"))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setTeams(Set.of(team)))
            .with(u -> u.setRoles(0L))
            .with(u -> u.setIsDisabled(false)).get();
    private final UserDTO userDTO = Builder.build(UserDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setKeycloakUid("abc-123"))
            .with(d -> d.setUsername("grogu"))
            .with(d -> d.setEmail("a.b@c"))
            .with(d -> d.setDisplayName("baby yoda"))
            .with(d -> d.setCreationDate(CREATION_DATE))
            .with(d -> d.setDodId(1L))
            .with(d -> d.setTeamIds(Set.of(1L)))
            .with(d -> d.setRoles(0L))
            .with(d -> d.setIsDisabled(false)).get();

    @Test
    public void should_have_all_userDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(User.class, fields::add);

        assertThat(fields.size()).isEqualTo(UserDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_set_and_get_properties() {

        assertThat(expectedUser.getId()).isEqualTo(1L);
        assertThat(expectedUser.getKeycloakUid()).isEqualTo("abc-123");
        assertThat(expectedUser.getUsername()).isEqualTo("grogu");
        assertThat(expectedUser.getEmail()).isEqualTo("a.b@c");
        assertThat(expectedUser.getDisplayName()).isEqualTo("baby yoda");
        assertThat(expectedUser.getCreationDate()).isEqualTo(CREATION_DATE);
        assertThat(expectedUser.getDodId()).isEqualTo(1);
        assertThat(expectedUser.getRoles()).isEqualTo(0L);
        assertThat(expectedUser.getIsDisabled()).isEqualTo(false);
    }

    @Test
    public void should_return_dto() {
        assertThat(expectedUser.toDto()).isEqualTo(userDTO);
    }

    @Test
    public void should_be_equal() {
        User user2 = new User();
        BeanUtils.copyProperties(expectedUser, user2);

        assertTrue(expectedUser.equals(expectedUser));
        assertFalse(expectedUser.equals(null));
        assertFalse(expectedUser.equals(new Team()));
        assertTrue(expectedUser.equals(user2));
        assertFalse(expectedUser.equals(new User()));
    }

}
