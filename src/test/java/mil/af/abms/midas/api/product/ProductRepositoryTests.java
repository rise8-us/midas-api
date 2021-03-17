package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    ProductRepository productRepository;

    @Test
    public void should_find_by_name() throws EntityNotFoundException {

        Team team = Builder.build(Team.class)
            .with(t -> t.setName("team"))
            .with(t -> t.setGitlabGroupId(1L))
            .with(t -> t.setDescription("for testing")).get();

        Team savedTeam = entityManager.persist(team);

        Product testProduct = Builder.build(Product.class)
                .with(p -> p.setGitlabProjectId(1L))
                .with(p -> p.setTeam(savedTeam))
                .with(p -> p.setName("foo")).get();
                
        entityManager.persist(testProduct);
        entityManager.flush();

        Product foundProduct = productRepository.findByName(testProduct.getName()).orElseThrow(() ->
            new EntityNotFoundException("Not Found"));

        assertThat(foundProduct.getTeam()).isEqualTo(savedTeam);
        assertThat(foundProduct).isEqualTo(testProduct);

        foundProduct.setTeam(null);

        entityManager.persist(foundProduct);
        entityManager.flush();

        Product productNoTeam = productRepository.findById(foundProduct.getId()).orElseThrow(() ->
            new EntityNotFoundException("Not found"));

        assertThat(productNoTeam.getTeam()).isEqualTo(null);
    }
}
