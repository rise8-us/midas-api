package mil.af.abms.midas.api.ogsm;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import mil.af.abms.midas.api.ogsm.dto.CreateOgsmDTO;
import mil.af.abms.midas.api.ogsm.dto.OgsmDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.AssertionType;

@ExtendWith(SpringExtension.class)
@Import(OgsmService.class)
public class OgsmServiceTests {
    
    @Autowired
    private OgsmService ogsmService;

    @MockBean
    private OgsmRepository ogsmRepository;
    @MockBean
    private AssertionService assertionService;
    @MockBean
    private UserService userService;
    @MockBean
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Ogsm> ogsmCaptor;

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime COMPLETE = NOW.plusWeeks(1L);

    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(1L)).get();
    private final User user = Builder.build(User.class)
            .with(p -> p.setId(2L)).get();
    private final Assertion objective = Builder.build(Assertion.class)
            .with(a -> a.setId(3L)).get();
    private final Ogsm ogsm = Builder.build(Ogsm.class)
            .with(o -> o.setId(4L))
            .with(o -> o.setProduct(product))
            .with(o -> o.setCreatedBy(user))
            .with(o -> o.setAssertions(Set.of(objective)))
            .with(o -> o.setCreationDate(NOW))
            .with(o -> o.setCompletedDate(COMPLETE))
            .get();
    private final OgsmDTO ogsmDTO = Builder.build(OgsmDTO.class)
            .with(d -> d.setId(4L))
            .with(d -> d.setProductId(product.getId()))
            .with(d -> d.setCreatedById(user.getId()))
            .with(d -> d.setAssertions(Set.of(objective.toDto())))
            .with(d -> d.setCreationDate(NOW))
            .with(d -> d.setCompletedDate(COMPLETE))
            .get();

    @Test
    public void should_create_objective() {
        CreateOgsmDTO createOgsmDTO = new CreateOgsmDTO(
                1L,
                Set.of(new CreateAssertionDTO("Make money", AssertionType.OBJECTIVE, 42L, Set.of())));


        when(userService.getUserBySecContext()).thenReturn(user);
        when(productService.getObject(1L)).thenReturn(product);
        when(ogsmRepository.save(any())).thenReturn(ogsm);
        when(assertionService.create(any())).thenReturn(objective);

        Ogsm withObjective = ogsmService.create(createOgsmDTO);

        verify(ogsmRepository, times(1)).save(ogsmCaptor.capture());
        Ogsm ogsmSaved = ogsmCaptor.getValue();

        assertThat(ogsmSaved.getCreatedBy()).isEqualTo(user);
        assertThat(withObjective.getAssertions()).isEqualTo(Set.of(objective));

    }

}
