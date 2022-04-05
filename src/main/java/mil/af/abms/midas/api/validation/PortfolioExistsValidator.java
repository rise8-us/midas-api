package mil.af.abms.midas.api.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.portfolio.PortfolioService;

public class PortfolioExistsValidator implements ConstraintValidator<PortfolioExists, Long> {

    @Autowired
    private PortfolioService portfolioService;

    @Setter
    private boolean allowNull;

    @Override
    public void initialize(PortfolioExists constraintAnnotation) { this.allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {
        return Optional.ofNullable(id).map(portfolioService::existsById).orElse(allowNull);
    }
}
