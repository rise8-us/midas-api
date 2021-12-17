package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserTypeTests {

    @Test
    void should_have_4_values() {
        assertThat(UserType.values().length).isEqualTo(4);
    }

    @Test
    void should_get_fields() {
        assertThat(UserType.ACTIVE.getName()).isEqualTo("ACTIVE");
        assertThat(UserType.ACTIVE.getDisplayName()).isEqualTo("Active");
        assertThat(UserType.ACTIVE.getDescription()).isEqualTo("Active user");
        assertThat(UserType.BOT.getName()).isEqualTo("BOT");
        assertThat(UserType.BOT.getDisplayName()).isEqualTo("Bot");
        assertThat(UserType.BOT.getDescription()).isEqualTo("External non-user entity");
        assertThat(UserType.DISABLED.getName()).isEqualTo("DISABLED");
        assertThat(UserType.DISABLED.getDisplayName()).isEqualTo("Disabled");
        assertThat(UserType.DISABLED.getDescription()).isEqualTo("Disabled user");
        assertThat(UserType.SYSTEM.getName()).isEqualTo("SYSTEM");
        assertThat(UserType.SYSTEM.getDisplayName()).isEqualTo("System");
        assertThat(UserType.SYSTEM.getDescription()).isEqualTo("Internal non-user entity");
    }
}
