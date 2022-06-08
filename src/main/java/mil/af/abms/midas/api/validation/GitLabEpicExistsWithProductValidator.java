package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.dtos.AddGitLabEpicWithProductDTO;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.product.ProductService;

public class GitLabEpicExistsWithProductValidator implements ConstraintValidator<GitLabEpicExistsForProduct, AddGitLabEpicWithProductDTO> {

    @Autowired
    private ProductService productService;

    @Autowired
    private EpicService epicService;

    @Override
    public boolean isValid(AddGitLabEpicWithProductDTO dto, ConstraintValidatorContext constraintContext) {
        try {
            var product = productService.findById(dto.getProductId());
            return epicService.canAddEpic(dto.getIId(), product);
        } catch (Exception e) {
            return false;
        }
    }

}
