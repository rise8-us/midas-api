package mil.af.abms.midas.api.dtos;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

import mil.af.abms.midas.api.validation.GitLabEpicExistsForPortfolio;

@Data
@AllArgsConstructor
@GitLabEpicExistsForPortfolio
public class AddGitLabEpicWithPortfolioDTO {

    @NotNull(message = "iId cannot be null")
    private Integer iId;

    @NotNull(message = "portfolioId cannot be null")
    private Long portfolioId;
}
