package us.rise8.mixer.api.helper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

import us.rise8.mixer.api.user.UserModel;

public class BuilderTests {

    @Test
    public void shouldBuildClassObject() {

        UserModel user = Builder.build(UserModel.class)
                .with(u -> u.setUsername("Foo")).get();
        assertThat(user.getUsername()).isEqualTo("Foo");
    }

}
