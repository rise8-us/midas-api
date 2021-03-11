package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.helper.Builder;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    ProductRepository productRepository;

    @Test
    public void should_Find_By_Name() {

        Product testProduct = Builder.build(Product.class)
                .with(u -> u.setGitlabProjectId(1L))
                .with(u -> u.setName("foo")).get();

        entityManager.persist(testProduct);
        entityManager.flush();

        Optional<Product> foundProduct = productRepository.findByName(testProduct.getName());

        assertThat(foundProduct.orElse(new Product())).isEqualTo(testProduct);
    }
}
