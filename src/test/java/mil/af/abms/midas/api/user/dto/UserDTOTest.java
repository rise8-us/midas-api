package mil.af.abms.midas.api.user.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import mil.af.abms.midas.api.helper.Builder;
import org.junit.jupiter.api.Test;

public class UserDTOTest {
    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final UserDTO userDTO = Builder.build(UserDTO.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setUsername("userA"))
            .with(u -> u.setEmail("userA@test"))
            .with(u -> u.setKeycloakUid("a-bc"))
            .with(u -> u.setDisplayName("user A"))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setDodId(2L))
            .with(u -> u.setRoles(0L))
            .with(u -> u.setIsDisabled(false)).get();
    private final UserDTO userDTO2 = Builder.build(UserDTO.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setUsername("userA"))
            .with(u -> u.setEmail("userA@test"))
            .with(u -> u.setKeycloakUid("a-bc"))
            .with(u -> u.setDisplayName("user A"))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setDodId(2L))
            .with(u -> u.setRoles(0L))
            .with(u -> u.setIsDisabled(false)).get();

    private final String userDTOString = String.format("UserDTO(id=1, keycloakUid=a-bc, username=userA, " +
            "email=userA@test, displayName=user A, creationDate=%s, dodId=2, isDisabled=false, roles=0)",
            CREATION_DATE.toString());

    @Test
    public void User_DTO_ToString() {
        assertThat(userDTO.toString()).isEqualTo(userDTOString);
    }

    @Test
    public void User_DTO_Getters() {
        assertThat(userDTO.getId()).isEqualTo(1L);
        assertThat(userDTO.getUsername()).isEqualTo("userA");
        assertThat(userDTO.getEmail()).isEqualTo("userA@test");
        assertThat(userDTO.getKeycloakUid()).isEqualTo("a-bc");
        assertThat(userDTO.getDisplayName()).isEqualTo("user A");
        assertThat(userDTO.getCreationDate()).isEqualTo(CREATION_DATE);
        assertThat(userDTO.getDodId()).isEqualTo(2L);
        assertThat(userDTO.getRoles()).isEqualTo(0l);
        assertFalse(userDTO.getIsDisabled());
    }

    @Test
    public void User_DTO_HashCode() {
        assertThat(userDTO.hashCode()).isEqualTo(userDTO2.hashCode());
    }

    @Test
    public void User_DTO_Equals() {
        assertTrue(userDTO.equals(userDTO2));
    }

}
