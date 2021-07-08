package mil.af.abms.midas.api.assertion;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.enums.AssertionStatus;
import mil.af.abms.midas.exception.EntityNotFoundException;

class AssertionSpecificationsTest extends RepositoryTestHarness {

    @Autowired
    AssertionRepository assertionRepository;

    private Product savedProduct;

    @BeforeEach
    void beforeEach() {
        Product product = Builder.build(Product.class)
                .with(p -> p.setName("Halo"))
                .get();

        savedProduct = entityManager.persist(product);

        LocalDateTime COMPLETED_DATE = LocalDateTime.now();
        Assertion assertion = Builder.build(Assertion.class)
                .with(a -> a.setProduct(savedProduct))
                .with(a -> a.setStatus(AssertionStatus.COMPLETED))
                .with(a -> a.setCompletedDate(COMPLETED_DATE))
                .get();

        entityManager.persist(assertion);
        entityManager.flush();
    }

    @Test
    public void should_throw_error_if_private_constructor_is_called() {
        Class<?> clazz = AssertionSpecifications.class;
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertThat(exception.getCause().getClass()).isEqualTo(IllegalStateException.class);
    }

    @Test
    void should_find_by_product_id() throws EntityNotFoundException {
        List<Assertion> assertions = assertionRepository.findAll(AssertionSpecifications.hasProductId(savedProduct.getId()));
        assertThat(assertions.size()).isEqualTo(1);
    }

    @Test
    void should_find_by_status() throws EntityNotFoundException {
        List<Assertion> assertions = assertionRepository.findAll(AssertionSpecifications.hasStatus(AssertionStatus.COMPLETED));
        assertThat(assertions.size()).isEqualTo(1);
    }
}