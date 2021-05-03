package mil.af.abms.midas.api.problem;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.problem.dto.CreateProblemDTO;
import mil.af.abms.midas.api.problem.dto.UpdateProblemDTO;
import mil.af.abms.midas.api.problem.dto.UpdateProblemIsCurrentDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;

@ExtendWith(SpringExtension.class)
@Import(ProblemService.class)
public class ProblemServiceTests {

    @Autowired
    ProblemService problemService;
    @MockBean
    UserService userService;
    @MockBean
    ProductService productService;
    @MockBean
    ProblemRepository problemRepository;
    @Captor
    ArgumentCaptor<Problem> problemCaptor;

    private static final LocalDateTime TEST_TIME = LocalDateTime.now();
    private final User createdBy = Builder.build(User.class).with(u -> u.setId(1L)).get();
    private final Product product = Builder.build(Product.class).with(p -> p.setId(1L)).get();
    private final Problem problem = Builder.build(Problem.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setText("no manning")).get();

    @Test
    public void should_create_problem() {
        CreateProblemDTO createProblemDTO = new CreateProblemDTO("No time", product.getId());

        when(userService.getUserBySecContext()).thenReturn(createdBy);
        when(productService.findByIdOrNull(anyLong())).thenReturn(product);
        when(problemRepository.save(any())).thenReturn(problem);

        problemService.create(createProblemDTO);

        verify(problemRepository, times(1)).save(problemCaptor.capture());
        Problem problemSaved = problemCaptor.getValue();

        assertThat(problemSaved.getText()).isEqualTo(createProblemDTO.getText());
        assertThat(problemSaved.getProduct().getId()).isEqualTo(createProblemDTO.getProductId());
        assertThat(problemSaved.getCreatedBy()).isEqualTo(createdBy);
        assertTrue(problemSaved.getIsCurrent());

    }

    @Test
    public void should_update_by_id() {
        UpdateProblemDTO updateProblemDTO = new UpdateProblemDTO("lost", product.getId());

        when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
        when(problemRepository.save(any())).thenReturn(problem);

        problemService.updateById(1L, updateProblemDTO);

        verify(problemRepository, times(1)).save(problemCaptor.capture());
        Problem problemSaved = problemCaptor.getValue();

        assertThat(problemSaved.getText()).isEqualTo(updateProblemDTO.getText());
    }

    @Test
    public void should_update_is_current_by_id() {
        UpdateProblemIsCurrentDTO isNotCurrentDTO = Builder.build(UpdateProblemIsCurrentDTO.class)
                .with(d -> d.setIsCurrent(false)).get();

        when(problemRepository.findById(any())).thenReturn(Optional.of(problem));
        when(problemRepository.save(any())).thenReturn(problem);

        problemService.updateIsCurrentById(1L, isNotCurrentDTO);

        verify(problemRepository, times(1)).save(problemCaptor.capture());
        Problem problemSaved = problemCaptor.getValue();

        assertFalse(problemSaved.getIsCurrent());
    }

    @Test
    public void should_create_problem_with_null_product_and_portfolio() {
        CreateProblemDTO createDTO = new CreateProblemDTO("manning", null);

        when(problemRepository.save(any())).thenReturn(problem);

        problemService.create(createDTO);

        verify(problemRepository, times(1)).save(problemCaptor.capture());
        Problem problemSaved = problemCaptor.getValue();

        assertThat(problemSaved.getProduct()).isEqualTo(null);
    }

    @Test
    public void should_update_problem_with_null_product_and_portfolio() {
        UpdateProblemDTO updateDTO = new UpdateProblemDTO("manning", null);

        when(problemRepository.findById(any())).thenReturn(Optional.of(problem));
        when(problemRepository.save(any())).thenReturn(problem);

        problemService.updateById(1L, updateDTO);

        verify(problemRepository, times(1)).save(problemCaptor.capture());
        Problem problemSaved = problemCaptor.getValue();

        assertThat(problemSaved.getProduct()).isEqualTo(null);
    }

}
