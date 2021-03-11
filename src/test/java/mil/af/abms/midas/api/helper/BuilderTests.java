package mil.af.abms.midas.api.helper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.user.User;

public class BuilderTests {

    @Test
    public void shouldBuildClassObject() {

        User user = Builder.build(User.class)
                .with(u -> u.setUsername("Foo")).get();
        assertThat(user.getUsername()).isEqualTo("Foo");
    }

}
