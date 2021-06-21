package mil.af.abms.midas.api.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;

class UserRepositoryTests extends RepositoryTestHarness {

    @Autowired
    UserRepository userRepository;

    @Test
    void should_find_by_username() {
         var testUser = Builder.build(User.class)
                .with(u -> u.setUsername("foo")).get();
        entityManager.persist(testUser);
        var userDelete = entityManager.persist(testUser);
        entityManager.remove(userDelete);
        entityManager.flush();

        var foundUser = userRepository.findByUsername(testUser.getUsername());

        assertThat(foundUser.orElse(new User())).isEqualTo(testUser);
    }

    @Test
    void should_find_by_keycloakUid() {
        var testUser = Builder.build(User.class)
                .with(u -> u.setUsername("foo"))
                .with(u -> u.setKeycloakUid("abc-123-efg")).get();

        entityManager.persist(testUser);
        entityManager.flush();

        var foundUser = userRepository.findByKeycloakUid(testUser.getKeycloakUid());

        assertThat(foundUser.orElse(new User())).isEqualTo(testUser);
    }

    @Test
    void should_delete() {
        var testUser = Builder.build(User.class)
                .with(u -> u.setUsername("foo")).get();
        var userDelete = entityManager.persist(testUser);

        entityManager.remove(userDelete);
        entityManager.flush();

        var foundUser = userRepository.findByUsername(testUser.getUsername());

        assertThat(foundUser.orElse(new User())).isEqualTo(new User());
    }
}
