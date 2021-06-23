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
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductRepository;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.ProductType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductTypeStrategyTests extends RepositoryTestHarness {

    private Product product = Builder.build(Product.class)
            .with(p -> p.setName("foo"))
            .with(p -> p.setType(ProductType.PRODUCT))
            .get();
    private  User user = Builder.build(User.class)
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("foo"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("Mr.Foo")).get();

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    void init() {
        user = entityManager.persist(user);
        product = entityManager.persist(product);
        entityManager.flush();
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_type_equal_to() {
        SearchCriteria criteria = new SearchCriteria("type", ":", null, "PRODUCT", null);
        Specification<Product> specs = new SpecificationImpl<>(criteria);
        List<Product> products = productRepository.findAll(specs);

        assertThat(products.get(0)).isEqualTo(product);
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_type_not_equalTo() {
        SearchCriteria criteria = new SearchCriteria("type", "!", null, "PRODUCT", null);
        Specification<Product> specs = new SpecificationImpl<>(criteria);
        List<Product> products = productRepository.findAll(specs);

        assertThat(products).isEmpty();
    }

    @Test
    void should_search_by_spec_and_parsing_strategy_type_null() {
        SearchCriteria criteria = new SearchCriteria("type", "::", null, "PRODUCT", null);
        Specification<Product> specs = new SpecificationImpl<>(criteria);
        List<Product> products = productRepository.findAll(specs);

        assertThat(products).hasSize(1);
    }

}
