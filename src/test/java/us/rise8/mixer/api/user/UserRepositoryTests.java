package us.rise8.mixer.api.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import us.rise8.mixer.api.helper.Builder;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    UserRepository userRepository;

    @Test
    public void shouldFindByUsername() {

        UserModel testUser = Builder.build(UserModel.class)
                .with(u -> u.setUsername("foo")).get();

        entityManager.persist(testUser);
        entityManager.flush();

        Optional<UserModel> foundUser = userRepository.findByUsername(testUser.getUsername());

        assertThat(foundUser.orElse(new UserModel())).isEqualTo(testUser);
    }

    @Test
    public void shouldFindByDodId() {
        UserModel testUser = Builder.build(UserModel.class)
                .with(u -> u.setDodId(1L)).get();

        entityManager.persist(testUser);
        entityManager.flush();

        Optional<UserModel> foundUser = userRepository.findByDodId(testUser.getDodId());

        assertThat(foundUser.orElse(new UserModel())).isEqualTo(testUser);
    }

    @Test
    public void shouldFindByKeycloakId() {
        UserModel testUser = Builder.build(UserModel.class)
                .with(u -> u.setKeycloakUid("abc-123-efg")).get();

        entityManager.persist(testUser);
        entityManager.flush();

        Optional<UserModel> foundUser = userRepository.findByKeycloakUid(testUser.getKeycloakUid());

        assertThat(foundUser.orElse(new UserModel())).isEqualTo(testUser);
    }

}
