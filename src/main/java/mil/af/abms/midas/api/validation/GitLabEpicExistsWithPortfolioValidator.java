package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.dtos.AddGitLabEpicWithPortfolioDTO;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.portfolio.PortfolioService;

public class GitLabEpicExistsWithPortfolioValidator implements ConstraintValidator<GitLabEpicExistsForPortfolio, AddGitLabEpicWithPortfolioDTO> {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private EpicService epicService;

    @Override
    public boolean isValid(AddGitLabEpicWithPortfolioDTO dto, ConstraintValidatorContext constraintContext) {
        try {
            var portfolio = portfolioService.findById(dto.getPortfolioId());
            return epicService.canAddEpic(dto.getIId(), portfolio);
        } catch (Exception e) {
            return false;
        }
    }

}
