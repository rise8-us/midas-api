package mil.af.abms.midas.api.epic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.exception.EntityNotFoundException;

public class EpicRepositoryTests extends RepositoryTestHarness {

    @Autowired
    EpicRepository epicRepository;

    private Personnel savedPersonnel;

    Personnel personnel = Builder.build(Personnel.class)
            .get();
    Epic epic = Builder.build(Epic.class)
            .with(e -> e.setEpicUid("2"))
            .get();
    Product product = Builder.build(Product.class)
            .with(p -> p.setName("Halo"))
            .with(p -> p.setPersonnel(savedPersonnel))
            .get();
    Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setName("Halo"))
            .with(p -> p.setPersonnel(savedPersonnel))
            .get();

    @Test
    void should_find_by_epicUid_with_product() throws EntityNotFoundException {
        Product savedProduct = entityManager.persist(product);
        savedPersonnel = entityManager.persist(personnel);

        epic.setProduct(savedProduct);
        Epic savedEpic = entityManager.persist(epic);

        entityManager.flush();

        Epic foundEpic = epicRepository.findByEpicUid(savedEpic.getEpicUid()).orElseThrow(() ->
                new EntityNotFoundException("Not Found"));

        assertThat(foundEpic.getEpicUid()).isEqualTo("2");
    }

    @Test
    void should_find_by_epicUid_with_portfolio() throws EntityNotFoundException {
        Portfolio savedPortfolio = entityManager.persist(portfolio);
        savedPersonnel = entityManager.persist(personnel);

        epic.setPortfolio(savedPortfolio);
        Epic savedEpic = entityManager.persist(epic);

        entityManager.flush();

        Epic foundEpic = epicRepository.findByEpicUid(savedEpic.getEpicUid()).orElseThrow(() ->
                new EntityNotFoundException("Not Found"));

        assertThat(foundEpic.getEpicUid()).isEqualTo("2");
    }

}
