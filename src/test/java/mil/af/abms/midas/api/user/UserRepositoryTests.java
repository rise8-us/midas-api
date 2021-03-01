package mil.af.abms.midas.api.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.helper.Builder;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    UserRepository userRepository;

    @Test
    public void should_Find_By_Username() {

        UserEntity testUser = Builder.build(UserEntity.class)
                .with(u -> u.setUsername("foo")).get();

        entityManager.persist(testUser);
        entityManager.flush();

        Optional<UserEntity> foundUser = userRepository.findByUsername(testUser.getUsername());

        assertThat(foundUser.orElse(new UserEntity())).isEqualTo(testUser);
    }

    @Test
    public void should_Find_By_DodId() {
        UserEntity testUser = Builder.build(UserEntity.class)
                .with(u -> u.setUsername("Foo"))
                .with(u -> u.setDodId(1L)).get();

        entityManager.persist(testUser);
        entityManager.flush();

        Optional<UserEntity> foundUser = userRepository.findByDodId(testUser.getDodId());

        assertThat(foundUser.orElse(new UserEntity())).isEqualTo(testUser);
    }

    @Test
    public void should_Find_By_KeycloakUId() {
        UserEntity testUser = Builder.build(UserEntity.class)
                .with(u -> u.setUsername("foo"))
                .with(u -> u.setKeycloakUid("abc-123-efg")).get();

        entityManager.persist(testUser);
        entityManager.flush();

        Optional<UserEntity> foundUser = userRepository.findByKeycloakUid(testUser.getKeycloakUid());

        assertThat(foundUser.orElse(new UserEntity())).isEqualTo(testUser);
    }
}
