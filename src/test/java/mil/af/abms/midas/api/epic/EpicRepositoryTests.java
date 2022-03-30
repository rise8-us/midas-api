package mil.af.abms.midas.api.epic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.exception.EntityNotFoundException;

public class EpicRepositoryTests extends RepositoryTestHarness {

    @Autowired
    EpicRepository epicRepository;

    private Product savedProduct;
    private Personnel savedPersonnel;

    @Test
    void should_find_by_epicUid() throws EntityNotFoundException {
        Personnel personnel = Builder.build(Personnel.class)
                .get();
        savedPersonnel = entityManager.persist(personnel);

        Product product = Builder.build(Product.class)
                .with(p -> p.setName("Halo"))
                .with(p -> p.setPersonnel(savedPersonnel))
                .get();
        savedProduct = entityManager.persist(product);

        Epic epic = Builder.build(Epic.class)
                .with(e -> e.setEpicUid("2"))
                .with(e -> e.setProduct(savedProduct))
                .get();
        Epic savedEpic = entityManager.persist(epic);

        entityManager.flush();

        Epic foundEpic = epicRepository.findByEpicUid(savedEpic.getEpicUid()).orElseThrow(() ->
                new EntityNotFoundException("Not Found"));

        assertThat(foundEpic.getEpicUid()).isEqualTo("2");
    }

}
