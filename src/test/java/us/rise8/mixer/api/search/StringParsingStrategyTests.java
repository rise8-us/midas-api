package us.rise8.mixer.api.search;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import us.rise8.mixer.api.helper.Builder;
import us.rise8.mixer.api.user.UserModel;
import us.rise8.mixer.api.user.UserRepository;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StringParsingStrategyTests {

    private final UserModel testUser1 = Builder.build(UserModel.class)
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("foo"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("Mr.Foo")).get();
    private final UserModel testUser2 = Builder.build(UserModel.class)
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
    public void shouldSearchBySpecAndParsingStratStringEqualTo() {
        SearchCriteria criteria = new SearchCriteria("username", ":", null, "foo", null);
        Specification<UserModel> specs = new SpecificationImpl<>(criteria);
        List<UserModel> users = userRepository.findAll(specs);

        assertThat(users.get(0).getUsername()).isEqualTo(testUser1.getUsername());
    }

    @Test
    public void shouldSearchBySpecAndParsingStratStringNotEqualTo() {
        SearchCriteria criteria = new SearchCriteria("username", "!", null, "foobar", null);
        Specification<UserModel> specs = new SpecificationImpl<>(criteria);
        List<UserModel> users = userRepository.findAll(specs);

        assertThat(users.get(0).getUsername()).isEqualTo(testUser1.getUsername());
    }

    @Test
    public void shouldSearchBySpecAndParsingStratStringGreaterThan() {
        SearchCriteria criteria = new SearchCriteria("username", ">", null, "fooba", null);
        Specification<UserModel> specs = new SpecificationImpl<>(criteria);
        List<UserModel> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getUsername()).isEqualTo(testUser2.getUsername());
    }

    @Test
    public void shouldSearchBySpecAndParsingStratStringLessThan() {
        SearchCriteria criteria = new SearchCriteria("username", "<", null, "foobar", null);
        Specification<UserModel> specs = new SpecificationImpl<>(criteria);
        List<UserModel> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getUsername()).isEqualTo(testUser1.getUsername());
    }

    @Test
    public void shouldSearchBySpecAndParsingStratStringStartsWith() {
        SearchCriteria criteria = new SearchCriteria("username", ":", null, "foo", "*");
        Specification<UserModel> specs = new SpecificationImpl<>(criteria);
        List<UserModel> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    public void shouldSearchBySpecAndParsingStratStringEndsWith() {
        SearchCriteria criteria = new SearchCriteria("username", ":", "*", "bar", null);
        Specification<UserModel> specs = new SpecificationImpl<>(criteria);
        List<UserModel> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getUsername()).isEqualTo(testUser2.getUsername());
    }

    @Test
    public void shouldSearchBySpecAndParsingStratStringContains() {
        SearchCriteria criteria = new SearchCriteria("username", ":", "*", "oo", "*");
        Specification<UserModel> specs = new SpecificationImpl<>(criteria);
        List<UserModel> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    public void shouldSearchBySpecAndParsingStratStringDoesNotStartsWith() {
        SearchCriteria criteria = new SearchCriteria("username", "!", null, "foo", "*");
        Specification<UserModel> specs = new SpecificationImpl<>(criteria);
        List<UserModel> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    public void shouldSearchBySpecAndParsingStratStringDoesNotEndsWith() {
        SearchCriteria criteria = new SearchCriteria("username", "!", "*", "bar", null);
        Specification<UserModel> specs = new SpecificationImpl<>(criteria);
        List<UserModel> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getUsername()).isEqualTo(testUser1.getUsername());
    }

    @Test
    public void shouldSearchBySpecAndParsingStratStringDoesNotContain() {
        SearchCriteria criteria = new SearchCriteria("username", "!", "*", "oo", "*");
        Specification<UserModel> specs = new SpecificationImpl<>(criteria);
        List<UserModel> users = userRepository.findAll(specs);

        assertThat(users.size()).isEqualTo(0);
    }

}
