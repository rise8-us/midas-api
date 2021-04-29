package mil.af.abms.midas.api.problem;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.problem.dto.CreateProblemDTO;
import mil.af.abms.midas.api.problem.dto.ProblemDTO;
import mil.af.abms.midas.api.problem.dto.UpdateProblemDTO;
import mil.af.abms.midas.api.problem.dto.UpdateProblemIsCurrentDTO;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.UserService;

@Service
public class ProblemService extends AbstractCRUDService<Problem, ProblemDTO, ProblemRepository> {
    
    private ProductService productService;
    private UserService userService;

    public ProblemService(ProblemRepository repository) {
        super(repository, Problem.class, ProblemDTO.class);
    }

    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }
    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Transactional
    public Problem create(CreateProblemDTO createProblemDTO) {
        Problem newProblem = Builder.build(Problem.class)
                .with(p -> p.setProblem(createProblemDTO.getProblem()))
                .with(p -> p.setCreatedBy(userService.getUserBySecContext()))
                .with(p -> p.setProduct(productService.findByIdOrNull(createProblemDTO.getProductId())))
                .get();

        return repository.save(newProblem);
    }

    @Transactional
    public Problem updateById(Long id, UpdateProblemDTO updateProblemDTO) {
        Problem problem = getObject(id);
        problem.setProblem(updateProblemDTO.getProblem());
        problem.setProduct(productService.findByIdOrNull(updateProblemDTO.getProductId()));

        return repository.save(problem);
    }

    @Transactional
    public Problem updateIsCurrentById(Long id, UpdateProblemIsCurrentDTO updateProblemIsCurrentDTO) {
        Problem problem = getObject(id);
        problem.setIsCurrent(updateProblemIsCurrentDTO.getIsCurrent());

        return repository.save(problem);
    }
}
