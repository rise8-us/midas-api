package mil.af.abms.midas.api.portfolio;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;

public interface PortfolioRepository extends RepositoryInterface<Portfolio, PortfolioDTO> {
    Optional<Portfolio> findByName(String name);
}
