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
import mil.af.abms.midas.api.feedback.FeedbackRepository;
import mil.af.abms.midas.api.feedback.Feedback;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.FeedbackRating;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FeedbackRatingStrategyTests extends RepositoryTestHarness {

    private Feedback feedback = Builder.build(Feedback.class)
            .with(f -> f.setId(1L))
            .with(f -> f.setRating(FeedbackRating.EXCELLENT))
            .get();
    private  User user = Builder.build(User.class)
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("foo"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("Mr.Foo")).get();

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    FeedbackRepository feedbackRepository;

    @BeforeEach
    void init() {
        user = entityManager.persist(user);
        feedback = entityManager.merge(feedback);
        entityManager.flush();
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_type_equal_to() {
        SearchCriteria criteria = new SearchCriteria("rating", ":", null, "EXCELLENT", null);
        Specification<Feedback> specs = new SpecificationImpl<>(criteria);
        List<Feedback> feedbacks = feedbackRepository.findAll(specs);

        assertThat(feedbacks.get(0)).isEqualTo(feedback);
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_type_not_equalTo() {
        SearchCriteria criteria = new SearchCriteria("rating", "!", null, "EXCELLENT", null);
        Specification<Feedback> specs = new SpecificationImpl<>(criteria);
        List<Feedback> feedbacks = feedbackRepository.findAll(specs);

        assertThat(feedbacks).isEmpty();
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_type_null() {
        SearchCriteria criteria = new SearchCriteria("rating", "::", null, "EXCELLENT", null);
        Specification<Feedback> specs = new SpecificationImpl<>(criteria);
        List<Feedback> feedbacks = feedbackRepository.findAll(specs);

        assertThat(feedbacks).hasSize(1);
    }

}
