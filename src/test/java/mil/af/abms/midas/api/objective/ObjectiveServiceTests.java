package mil.af.abms.midas.api.objective;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.objective.dto.CreateObjectiveDTO;
import mil.af.abms.midas.api.objective.dto.ObjectiveDTO;
import mil.af.abms.midas.api.objective.dto.UpdateObjectiveDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.AssertionType;

@ExtendWith(SpringExtension.class)
@Import(ObjectiveService.class)
public class ObjectiveServiceTests {
    
    @Autowired
    private ObjectiveService objectiveService;

    @MockBean
    private ObjectiveRepository objectiveRepository;
    @MockBean
    private AssertionService assertionService;
    @MockBean
    private UserService userService;
    @MockBean
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Objective> objectiveCaptor;

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime COMPLETE = NOW.plusWeeks(1L);

    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(1L)).get();
    private final User user = Builder.build(User.class)
            .with(p -> p.setId(2L)).get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(3L)).get();
    private final Objective objective = Builder.build(Objective.class)
            .with(o -> o.setId(4L))
            .with(o -> o.setProduct(product))
            .with(o -> o.setCreatedBy(user))
            .with(o -> o.setAssertions(Set.of(assertion)))
            .with(o -> o.setCreationDate(NOW))
            .with(o -> o.setCompletedDate(COMPLETE))
            .get();
    private final ObjectiveDTO objectiveDTO = Builder.build(ObjectiveDTO.class)
            .with(d -> d.setId(4L))
            .with(d -> d.setProductId(product.getId()))
            .with(d -> d.setCreatedById(user.getId()))
            .with(d -> d.setAssertionIds(Set.of(assertion.getId())))
            .with(d -> d.setCreationDate(NOW))
            .with(d -> d.setCompletedDate(COMPLETE))
            .get();

    @Test
    public void should_create_objective() {
        CreateAssertionDTO createAssertionDTO = new CreateAssertionDTO("Make money", AssertionType.OBJECTIVE,
                42L, Set.of(),null, Set.of(), null
        );
        CreateObjectiveDTO createObjectiveDTO = new CreateObjectiveDTO(1L, "text",
                Set.of(createAssertionDTO)
        );

        when(userService.getUserBySecContext()).thenReturn(user);
        when(productService.getObject(1L)).thenReturn(product);
        when(objectiveRepository.save(any())).thenReturn(objective);
        when(assertionService.linkAndCreateAssertions(any())).thenReturn(Set.of(assertion));

        Objective withObjective = objectiveService.create(createObjectiveDTO);

        verify(objectiveRepository, times(1)).save(objectiveCaptor.capture());
        Objective objectiveSaved = objectiveCaptor.getValue();

        assertThat(objectiveSaved.getCreatedBy()).isEqualTo(user);
        assertThat(objectiveSaved.getText()).isEqualTo("text");
        assertThat(withObjective.getAssertions()).isEqualTo(Set.of(assertion));
    }

    @Test
    public void should_update_objective_by_id() {
        UpdateObjectiveDTO updateObjectiveDTO = new UpdateObjectiveDTO("Make money", null);

        when(objectiveRepository.findById(4L)).thenReturn(Optional.of(objective));
        when(objectiveRepository.save(any())).thenReturn(objective);

        objectiveService.updateById(updateObjectiveDTO, 4L);

        verify(objectiveRepository, times(1)).save(objectiveCaptor.capture());
        Objective objectiveSaved = objectiveCaptor.getValue();

        assertThat(objectiveSaved.getText()).isEqualTo(updateObjectiveDTO.getText());
    }



}
