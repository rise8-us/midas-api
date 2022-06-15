package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.dtos.AppGroupDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.exception.EntityNotFoundException;

public class UniqueGroupIdAndSourceControlValidator implements ConstraintValidator<UniqueGroupIdAndSourceControl, AppGroupDTO> {

    @Autowired
    private ProductService productService;
    @Autowired
    private PortfolioService portfolioService;

    @Override
    public boolean isValid(AppGroupDTO dto, ConstraintValidatorContext constraintContext) {

        Integer dtoGroupId = dto.getGitlabGroupId();
        Long dtoSourceControlId = dto.getSourceControlId();
        if (dtoGroupId == null || dtoSourceControlId == null) {
            return true;
        }

        List<Product> allProducts = productService.getAll();
        List<Portfolio> allPortfolios = portfolioService.getAll();

        for (Product product : allProducts) {
            Integer groupId = product.getGitlabGroupId();
            Long sourceControlId = Optional.ofNullable(product.getSourceControl()).isPresent() ? product.getSourceControl().getId() : null;

            if (groupId != null && sourceControlId != null) {
                boolean isDuplicate = dtoGroupId.equals(groupId) && dtoSourceControlId.equals(sourceControlId);

                try {
                    Product foundProduct = productService.findByName(dto.getName());
                    if (isDuplicate && !foundProduct.getId().equals(product.getId())) {
                        return false;
                    }
                } catch (EntityNotFoundException e) {
                    if (isDuplicate) {
                        return false;
                    }
                }
            }
        }

        for (Portfolio portfolio : allPortfolios) {
            Integer groupId = portfolio.getGitlabGroupId();
            Long sourceControlId = Optional.ofNullable(portfolio.getSourceControl()).isPresent() ? portfolio.getSourceControl().getId() : null;

            if (groupId != null && sourceControlId != null) {
                boolean isDuplicate = dtoGroupId.equals(groupId) && dtoSourceControlId.equals(sourceControlId);

                try {
                    Portfolio foundPortfolio = portfolioService.findByName(dto.getName());
                    if (isDuplicate && !foundPortfolio.getId().equals(portfolio.getId())) {
                        return false;
                    }
                } catch (EntityNotFoundException e) {
                    if (isDuplicate) {
                        return false;
                    }
                }
            }
        }

       return true;
    }

}
