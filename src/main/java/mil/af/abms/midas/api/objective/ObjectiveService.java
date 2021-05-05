package mil.af.abms.midas.api.objective;

import javax.transaction.Transactional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.objective.dto.CreateObjectiveDTO;
import mil.af.abms.midas.api.objective.dto.ObjectiveDTO;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.UserService;

@Service
public class ObjectiveService extends AbstractCRUDService<Objective, ObjectiveDTO, ObjectiveRepository> {

    private UserService userService;
    private ProductService productService;
    private AssertionService assertionService;

    @Autowired
    public ObjectiveService(ObjectiveRepository repository) {
        super(repository, Objective.class, ObjectiveDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }
    @Autowired
    void setProductService(ProductService productService) { this.productService = productService; }
    @Autowired
    void setAssertionService(AssertionService assertionService) { this.assertionService = assertionService; }

    @Transactional
    public Objective create(CreateObjectiveDTO createObjectiveDTO) {
        Objective newObjective = Builder.build(Objective.class)
                .with(o -> o.setCreatedBy(userService.getUserBySecContext()))
                .with(o -> o.setText(createObjectiveDTO.getText()))
                .with(o -> o.setProduct(productService.getObject(createObjectiveDTO.getProductId())))
                .get();
        Objective savedObjective = repository.save(newObjective);

        savedObjective.setAssertions(
                createObjectiveDTO.getAssertionDTOs().stream().map(a -> {
                        a.setObjectiveId(savedObjective.getId());
                        return assertionService.create(a);
                }).collect(Collectors.toSet())
        );

        return savedObjective;
    }


}
