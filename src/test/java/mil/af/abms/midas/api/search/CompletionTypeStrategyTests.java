package mil.af.abms.midas.api.search;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.completion.Completion;
import mil.af.abms.midas.api.completion.CompletionRepository;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.CompletionType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CompletionTypeStrategyTests extends RepositoryTestHarness {

    private Completion completion = Builder.build(Completion.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setCompletionType(CompletionType.BINARY))
            .get();
    private  User user = Builder.build(User.class)
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("foo"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("Mr.Foo")).get();

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    CompletionRepository completionRepository;

    @BeforeEach
    void init() {
        user = entityManager.persist(user);
        completion = entityManager.merge(completion);
        entityManager.flush();
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_type_equal_to() {
        SearchCriteria criteria = new SearchCriteria("completionType", ":", null, "BINARY", null);
        Specification<Completion> specs = new SpecificationImpl<>(criteria);
        List<Completion> completions = completionRepository.findAll(specs);

        assertThat(completions.get(0)).isEqualTo(completion);
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_type_not_equalTo() {
        SearchCriteria criteria = new SearchCriteria("completionType", "!", null, "BINARY", null);
        Specification<Completion> specs = new SpecificationImpl<>(criteria);
        List<Completion> completions = completionRepository.findAll(specs);

        assertThat(completions).isEmpty();
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_type_null() {
        SearchCriteria criteria = new SearchCriteria("completionType", "::", null, "BINARY", null);
        Specification<Completion> specs = new SpecificationImpl<>(criteria);
        List<Completion> completions = completionRepository.findAll(specs);

        assertThat(completions).hasSize(1);
    }

}
