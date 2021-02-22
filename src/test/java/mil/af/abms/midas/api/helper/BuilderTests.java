package mil.af.abms.midas.api.helper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import mil.af.abms.midas.api.user.UserModel;
import org.junit.jupiter.api.Test;

public class BuilderTests {

    @Test
    public void shouldBuildClassObject() {

        UserModel user = Builder.build(UserModel.class)
                .with(u -> u.setUsername("Foo")).get();
        assertThat(user.getUsername()).isEqualTo("Foo");
    }

}
