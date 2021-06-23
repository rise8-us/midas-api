package mil.af.abms.midas.api.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.product.ProductService;

public class ProductExistsValidator implements ConstraintValidator<ProductExists, Long> {

    @Autowired
    private ProductService productService;

    @Setter
    private boolean allowNull;

    @Override
    public void initialize(ProductExists constraintAnnotation) { this.allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {
        return Optional.ofNullable(id).map(productService::existsById).orElse(allowNull);
    }
}
