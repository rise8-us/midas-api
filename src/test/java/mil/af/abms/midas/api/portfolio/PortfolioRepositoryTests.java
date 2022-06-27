package mil.af.abms.midas.api.portfolio;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.exception.EntityNotFoundException;

class PortfolioRepositoryTests extends RepositoryTestHarness {

    @Autowired
    PortfolioRepository portfolioRepository;

    private Portfolio portfolio;

    @BeforeEach
    void beforeEach() {
        portfolio = Builder.build(Portfolio.class)
                .with(p -> p.setName("foo"))
                .get();

        entityManager.persist(portfolio);
        entityManager.flush();
    }

    @Test
    void should_find_by_name() throws EntityNotFoundException {
        Portfolio foundPortfolio = portfolioRepository.findByName(portfolio.getName()).orElseThrow(() ->
                new EntityNotFoundException("Not Found"));

        assertThat(foundPortfolio.getName()).isEqualTo(portfolio.getName());
    }
}
