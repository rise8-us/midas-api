package mil.af.abms.midas.api.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.TeamEntity;
import mil.af.abms.midas.api.user.dto.UserDTO;

public class UserEntityTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final UserEntity expectedUser = Builder.build(UserEntity.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("grogu"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("baby yoda"))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setRoles(0L))
            .with(u -> u.setIsDisabled(false)).get();

    private final UserDTO userDTO = expectedUser.toDto();

    @Test
    public void should_Set_And_Get_Properties() {

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
    public void canReturnDTO() {
        assertThat(expectedUser.toDto()).isEqualTo(userDTO);
    }

    @Test
    public void should_Be_Equal() {
        UserEntity user2 = Builder.build(UserEntity.class)
                .with(u -> u.setKeycloakUid("abc-123")).get();

        assertTrue(expectedUser.equals(expectedUser));
        assertFalse(expectedUser.equals(null));
        assertFalse(expectedUser.equals(new TeamEntity()));
        assertTrue(expectedUser.equals(user2));
    }
}
