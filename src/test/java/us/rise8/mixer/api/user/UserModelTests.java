package us.rise8.mixer.api.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import us.rise8.mixer.api.helper.Builder;
import us.rise8.mixer.api.user.dto.UserDTO;

public class UserModelTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final UserDTO userDTO = Builder.build(UserDTO.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("grogu"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("baby yoda"))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setIsDisabled(false))
            .with(u -> u.setRoles(0L)).get();
    private final UserModel expectedUser = Builder.build(UserModel.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("grogu"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("baby yoda"))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setDodId(1L))
            .with(u -> u.setRoles(0L))
            .with(u -> u.setIsDisabled(false)).get();

    @Test
    public void shouldSetAndGetProperties() {
        UserModel user = Builder.build(UserModel.class)
                .with(u -> u.setId(1L))
                .with(u -> u.setKeycloakUid("abc-123"))
                .with(u -> u.setUsername("foo"))
                .with(u -> u.setEmail("a.b@c"))
                .with(u -> u.setDisplayName("baby yoda"))
                .with(u -> u.setCreationDate(CREATION_DATE))
                .with(u -> u.setDodId(1L))
                .with(u -> u.setRoles(0L))
                .with(u -> u.setIsDisabled(false)).get();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getKeycloakUid()).isEqualTo("abc-123");
        assertThat(user.getUsername()).isEqualTo("foo");
        assertThat(user.getEmail()).isEqualTo("a.b@c");
        assertThat(user.getDisplayName()).isEqualTo("baby yoda");
        assertThat(user.getCreationDate()).isEqualTo(CREATION_DATE);
        assertThat(user.getDodId()).isEqualTo(1);
        assertThat(user.getRoles()).isEqualTo(0L);
        assertThat(user.getIsDisabled()).isEqualTo(false);
    }

    @Test
    public void canReturnDTO() {
        assertThat(expectedUser.toDto()).isEqualTo(userDTO);
    }

    @Test
    public void shouldCreateUserFromUserDTO() {
        UserModel user = UserModel.fromDTO(userDTO);
        assertThat(user).isEqualTo(expectedUser);
    }

}
