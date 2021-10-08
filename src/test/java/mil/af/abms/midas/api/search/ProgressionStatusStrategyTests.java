package mil.af.abms.midas.api.search;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionRepository;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.AssertionType;
import mil.af.abms.midas.enums.ProductType;
import mil.af.abms.midas.enums.ProgressionStatus;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProgressionStatusStrategyTests extends RepositoryTestHarness {

    private Product product = Builder.build(Product.class)
            .with(p -> p.setName("foo"))
            .with(p -> p.setType(ProductType.PRODUCT))
            .get();
    private  User user = Builder.build(User.class)
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("foo"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("Mr.Foo")).get();
    private final Assertion goal = Builder.build(Assertion.class)
            .with(a -> a.setText("goal"))
            .with(a -> a.setType(AssertionType.GOAL))
            .with(a -> a.setStatus(ProgressionStatus.NOT_STARTED))
            .with(a -> a.setCreationDate(LocalDateTime.now()))
            .with(a -> a.setCompletedDate(LocalDateTime.now()))
            .get();
    private final Assertion strategy = Builder.build(Assertion.class)
            .with(a -> a.setText("strategy"))
            .with(a -> a.setType(AssertionType.STRATEGY))
            .with(a -> a.setStatus(ProgressionStatus.ON_TRACK))
            .with(a -> a.setCreationDate(LocalDateTime.now()))
            .with(a -> a.setCompletedDate(LocalDateTime.now()))
            .get();

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    AssertionRepository assertionRepository;

    @BeforeEach
    void init() {
        user = entityManager.persist(user);
        product = entityManager.persist(product);
        goal.setProduct(product);
        goal.setCreatedBy(user);
        strategy.setProduct(product);
        strategy.setCreatedBy(user);
        entityManager.persist(goal);
        entityManager.persist(strategy);
        entityManager.flush();
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_status_equal_to() {
        SearchCriteria criteria = new SearchCriteria("status", ":", null, "ON_TRACK", null);
        Specification<Assertion> specs = new SpecificationImpl<>(criteria);
        List<Assertion> assertions = assertionRepository.findAll(specs);

        assertThat(assertions).hasSize(1);
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_status_not_equalTo() {
        SearchCriteria criteria = new SearchCriteria("status", "!", null, "ON_TRACK", null);
        Specification<Assertion> specs = new SpecificationImpl<>(criteria);
        List<Assertion> assertions = assertionRepository.findAll(specs);

        assertThat(assertions).hasSize(1);
    }

    @Test
    void should_return_null_strategy() {
        SearchCriteria criteria = new SearchCriteria("status", "::", null, "ON_TRACK", null);
        Specification<Assertion> specs = new SpecificationImpl<>(criteria);
        List<Assertion> assertions = assertionRepository.findAll(specs);

        assertThat(assertions).hasSize(2);
    }

}
