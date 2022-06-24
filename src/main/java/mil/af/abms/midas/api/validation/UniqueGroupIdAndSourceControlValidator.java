package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.dtos.AppGroupDTO;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.ProductService;

public class UniqueGroupIdAndSourceControlValidator implements ConstraintValidator<UniqueGroupIdAndSourceControl, AppGroupDTO> {

    @Autowired
    private ProductService productService;
    @Autowired
    private PortfolioService portfolioService;

    @Override
    public boolean isValid(AppGroupDTO dto, ConstraintValidatorContext constraintContext) {
        if (dto.getGitlabGroupId() == null || dto.getSourceControlId() == null) { return true; }

        boolean productUnique = productService.validateUniqueSourceControlAndGitlabGroup(dto);
        boolean portfolioUnique = portfolioService.validateUniqueSourceControlAndGitlabGroup(dto);

        return productUnique && portfolioUnique;
    }

}
