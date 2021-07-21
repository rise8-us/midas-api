package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.product.ProductService;

public class ProductsExistValidator implements ConstraintValidator<ProductsExist, Set<Long>> {

    @Autowired
    private ProductService productService;

    @Override
    public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
        constraintContext.disableDefaultConstraintViolation();

        var violations = ids.stream().filter(i -> !productService.existsById(i)).map(i ->
            constraintContext.buildConstraintViolationWithTemplate(
                        String.format("Product with id: %s does not exists", i))
                    .addConstraintViolation()
        ).collect(Collectors.toList());

        return violations.isEmpty();
    }
    
}
