package mil.af.abms.midas.api.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.enums.ProgressionStatus;
import mil.af.abms.midas.exception.EntityNotFoundException;

class AssertionSpecificationsTest extends RepositoryTestHarness {

    @Autowired
    AssertionRepository assertionRepository;

    private Product savedProduct;
    private Personnel savedPersonnel;

    @BeforeEach
    void beforeEach() {
        Personnel personnel = Builder.build(Personnel.class)
                .get();
        savedPersonnel = entityManager.persist(personnel);

        Product product = Builder.build(Product.class)
                .with(p -> p.setName("Halo"))
                .with(p -> p.setPersonnel(savedPersonnel))
                .get();
        savedProduct = entityManager.persist(product);

        Assertion assertion = Builder.build(Assertion.class)
                .with(a -> a.setProduct(savedProduct))
                .with(a -> a.setStatus(ProgressionStatus.COMPLETED))
                .with(a -> a.setMeasures(Set.of()))
                .get();
        entityManager.persist(assertion);

        entityManager.flush();
    }

    @Test
    void should_throw_error_if_private_constructor_is_called() {
        Class<?> clazz = AssertionSpecifications.class;
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertThat(exception.getCause().getClass()).isEqualTo(IllegalStateException.class);
    }

    @Test
    void should_find_by_product_id() throws EntityNotFoundException {
        List<Assertion> assertions = assertionRepository.findAll(AssertionSpecifications.hasProductId(savedProduct.getId()));
        assertThat(assertions).hasSize(1);
    }

    @Test
    void should_find_by_status() throws EntityNotFoundException {
        List<Assertion> assertions = assertionRepository.findAll(AssertionSpecifications.hasStatus(ProgressionStatus.COMPLETED));
        assertThat(assertions).hasSize(1);
    }
}
