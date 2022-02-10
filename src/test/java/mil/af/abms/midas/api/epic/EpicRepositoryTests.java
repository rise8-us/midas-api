package mil.af.abms.midas.api.epic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.exception.EntityNotFoundException;
import mil.af.abms.midas.api.product.Product;

public class EpicRepositoryTests extends RepositoryTestHarness {

    @Autowired
    EpicRepository epicRepository;

    @Test
    void should_find_by_epicUid() throws EntityNotFoundException {
        Product mock = Builder.build(Product.class).with(p -> p.setName("name")).get();

        entityManager.persist(mock);
        entityManager.flush();

        Epic epic = Builder.build(Epic.class)
                .with(e -> e.setEpicUid("2"))
                .with(e -> e.setProduct(mock))
                .get();

        Epic savedEpic = entityManager.persist(epic);
        entityManager.flush();

        Epic foundEpic = epicRepository.findByEpicUid(savedEpic.getEpicUid()).orElseThrow(() ->
                new EntityNotFoundException("Not Found"));

        assertThat(foundEpic.getEpicUid()).isEqualTo("2");
    }

}
