package mil.af.abms.midas.api.search;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserRepository;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StringParsingStrategyTests extends RepositoryTestHarness {

    private final User testUser1 = Builder.build(User.class)
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("foo"))
            .with(u -> u.setEmail("a.b@c"))
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
    void init() {
        entityManager.persist(testUser1);
        entityManager.persist(testUser2);
        entityManager.flush();
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_equalTo() {
        SearchCriteria criteria = new SearchCriteria("username", ":", null, "foo", null);
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users.get(0).getUsername()).isEqualTo(testUser1.getUsername());
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_not_equalTo() {
        SearchCriteria criteria = new SearchCriteria("username", "!", null, "foobar", null);
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users.get(0).getUsername()).isEqualTo(testUser1.getUsername());
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_greater_than() {
        SearchCriteria criteria = new SearchCriteria("username", ">", null, "fooba", null);
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getUsername()).isEqualTo(testUser2.getUsername());
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_less_than() {
        SearchCriteria criteria = new SearchCriteria("username", "<", null, "foobar", null);
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getUsername()).isEqualTo(testUser1.getUsername());
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_null() {
        SearchCriteria criteria = new SearchCriteria("username", "<=", null, "foobar", null);
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users).hasSize(2);
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_starts_with() {
        SearchCriteria criteria = new SearchCriteria("username", ":", null, "foo", "*");
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users).hasSize(2);
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_ends_with() {
        SearchCriteria criteria = new SearchCriteria("username", ":", "*", "bar", null);
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getUsername()).isEqualTo(testUser2.getUsername());
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_contains() {
        SearchCriteria criteria = new SearchCriteria("username", ":", "*", "oo", "*");
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users).hasSize(2);
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_does_not_starts_with() {
        SearchCriteria criteria = new SearchCriteria("username", "!", null, "foo", "*");
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users).isEmpty();
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_does_not_ends_with() {
        SearchCriteria criteria = new SearchCriteria("username", "!", "*", "bar", null);
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getUsername()).isEqualTo(testUser1.getUsername());
    }

    @Test
    void should_search_by_spec_and_parsing_strat_string_does_not_contain() {
        SearchCriteria criteria = new SearchCriteria("username", "!", "*", "oo", "*");
        Specification<User> specs = new SpecificationImpl<>(criteria);
        List<User> users = userRepository.findAll(specs);

        assertThat(users).isEmpty();
    }

}
