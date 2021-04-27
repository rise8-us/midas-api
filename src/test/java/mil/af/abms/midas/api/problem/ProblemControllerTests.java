package mil.af.abms.midas.api.problem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.problem.dto.CreateProblemDTO;
import mil.af.abms.midas.api.problem.dto.UpdateProblemDTO;
import mil.af.abms.midas.api.problem.dto.UpdateProblemIsCurrentDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;

@WebMvcTest({ProblemController.class})
public class ProblemControllerTests extends ControllerTestHarness {

    @MockBean
    ProblemService problemService;
    @MockBean
    PortfolioService portfolioService;
    @MockBean
    ProductService productService;

    private final static LocalDateTime TEST_TIME = LocalDateTime.now();

    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Product product = Builder.build(Product.class).with(p -> p.setId(3L)).get();
    private final Portfolio portfolio = Builder.build(Portfolio.class).with(p -> p.setId(3L)).get();
    private final CreateProblemDTO createProblemDTO = new CreateProblemDTO("manning", product.getId(), portfolio.getId());
    private final UpdateProblemDTO updateProblemDTO = new UpdateProblemDTO("security",  product.getId(), portfolio.getId());
    private final Problem problem = Builder.build(Problem.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setProblem("Not enough time"))
            .with(p -> p.setPortfolio(portfolio))
            .with(p -> p.setProduct(product))
            .with(p -> p.setCreatedBy(createdBy))
            .with(p -> p.setIsCurrent(true))
            .with(p -> p.setCreationDate(TEST_TIME)).get();


    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_problem() throws Exception {
        when(problemService.create(any(CreateProblemDTO.class))).thenReturn(problem);

        mockMvc.perform(post("/api/problems")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createProblemDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.problem").value("Not enough time"));
    }

    @Test
    public void should_update_by_id() throws Exception {
        when(problemService.updateById(any(), any(UpdateProblemDTO.class))).thenReturn(problem);

        mockMvc.perform(put("/api/problems/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateProblemDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.problem").value("Not enough time"));
    }

    @Test
    public void should_toggle_problem_is_current() throws Exception {
        UpdateProblemIsCurrentDTO notCurrentDTO = Builder.build(UpdateProblemIsCurrentDTO.class)
                .with(d -> d.setIsCurrent(false)).get();
        problem.setIsCurrent(false);

        when(problemService.updateIsCurrentById(1L, notCurrentDTO)).thenReturn(problem);

        mockMvc.perform(put("/api/problems/1/current")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(notCurrentDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isCurrent").value(problem.getIsCurrent()));
    }
}

