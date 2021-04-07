package mil.af.abms.midas.api.portfolio.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.helper.HttpPathVariableIdGrabber;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.exception.EntityNotFoundException;

public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {

    @Autowired
    private PortfolioService portfolioService;

    @Setter
    private boolean isNew;

    @Override
    public void initialize(UniqueName constraintAnnotation) { this.isNew = constraintAnnotation.isNew(); }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintContext) {
        try {
            Portfolio existingPortfolio = portfolioService.findByName(name);
            if (isNew) {
                return false;
            } else {
                return HttpPathVariableIdGrabber.getPathId().equals(existingPortfolio.getId());
            }
        } catch (EntityNotFoundException e) {
            return true;
        }
    }
}
