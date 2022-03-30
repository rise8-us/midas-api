package mil.af.abms.midas.api.portfolio.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.exception.EntityNotFoundException;
import mil.af.abms.midas.helpers.RequestContext;

@ExtendWith(SpringExtension.class)
@Import({UniquePortfolioNameValidator.class})
public class UniquePortfolioNameValidatorTests {

    private final Portfolio foundPortfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("ABMS")).get();

    @Autowired
    UniquePortfolioNameValidator validator;
    @MockBean
    private PortfolioService portfolioService;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @AfterEach
    public void tearDown() {
        clearRequestContext();
    }

    @Test
    public void should_validate_new_portfolio_true() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(true);

        when(portfolioService.findByName("ABMS")).thenThrow(new EntityNotFoundException("Portfolio"));

        assertTrue(validator.isValid(foundPortfolio.getName(), context));
    }

    @Test
    public void should_validate_new_portfolio_false() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(true);

        when(portfolioService.findByName("ABMS")).thenReturn(foundPortfolio);

        assertFalse(validator.isValid(foundPortfolio.getName(), context));
    }

    @Test
    public void should_validate_update_portfolio_true() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(false);

        when(portfolioService.findByName(any())).thenReturn(foundPortfolio);

        assertTrue(validator.isValid(foundPortfolio.getName(), context));
    }

    @Test
    public void should_validate_update_portfolio_false() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(false);

        when(portfolioService.findByName(any())).thenReturn(foundPortfolio);

        assertFalse(validator.isValid(foundPortfolio.getName(), context));
    }

    private void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}
