package mil.af.abms.midas.api.assertion;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.AssertionStatus;

@Service
public class AssertionService extends AbstractCRUDService<Assertion, AssertionDTO, AssertionRepository> {

    private UserService userService;
    private ProductService productService;

    public AssertionService(AssertionRepository repository) {
        super(repository, Assertion.class, AssertionDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }

    @Transactional
    public Assertion create(CreateAssertionDTO dto) {
        Assertion newAssertion = Builder.build(Assertion.class)
                .with(a -> a.setText(dto.getText()))
                .with(a -> a.setType(dto.getType()))
                .with(a -> a.setStatus(AssertionStatus.NOT_STARTED))
                .with(a -> a.setProduct(productService.getObject(dto.getProductId())))
                .with(a -> a.setParent(findByIdOrNull(dto.getParentId())))
                .with(a -> a.setCreatedBy(userService.getUserBySecContext()))
                .get();
        newAssertion = repository.save(newAssertion);
        Long parentId = newAssertion.getId();
        newAssertion.setChildren(dto.getChildren().stream().map(d -> {
            d.setParentId(parentId);
            return this.create(d);
        }).collect(Collectors.toSet()));
        return newAssertion;
    }

    @Transactional
    public Assertion updateById(Long id, UpdateAssertionDTO dto) {
        Assertion assertion = getObject(id);

        Set<Assertion> newChildren = dto.getChildren().stream().map(d -> {
            d.setParentId(id);
            return this.create(d);
        }).collect(Collectors.toSet());
        assertion.getChildren().addAll(newChildren);
        assertion.setStatus(dto.getStatus());
        assertion.setText(dto.getText());

        return repository.save(assertion);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        getObject(id).getChildren().forEach(a -> deleteById(a.getId()));
        repository.deleteById(id);
    }

}
