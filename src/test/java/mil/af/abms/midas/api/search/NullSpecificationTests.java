package mil.af.abms.midas.api.search;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserRepository;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NullSpecificationTests {

    private final User testUser1 = Builder.build(User.class)
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("foobar"))
            .with(u -> u.setEmail("d.e@f"))
            .with(u -> u.setDisplayName("Mr.Foo")).get();
    private final User testUser2 = Builder.build(User.class)
            .with(u -> u.setKeycloakUid("def-456"))
            .with(u -> u.setUsername("foobar"))
            .with(u -> u.setEmail("d.e@f"))
            .with(u -> u.setDisplayName("foobar")).get();
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void init() {
        entityManager.persist(testUser1);
        entityManager.persist(testUser2);
        entityManager.flush();
    }

    @Test
    public void should_use_null_parse_strategy() {
        Specification<User> specs = new NullSpecification<>();
        List<User> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(2);
    }
}
