package mil.af.abms.midas.api.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import({UniqueGroupIdAndSourceControlValidator.class})
public class UniqueGroupIdAndSourceControlTests {

    @Autowired
    UniqueGroupIdAndSourceControlValidator validator;
    @MockBean
    private ProductService productService;
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

    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(s -> s.setId(3L)).get();
    private final Product foundProduct = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Found Product"))
            .with(p -> p.setGitlabGroupId(123))
            .with(p -> p.setSourceControl(sourceControl))
            .get();
    private final Portfolio foundPortfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(2L))
            .with(p -> p.setName("Found Portfolio"))
            .with(p -> p.setGitlabGroupId(1234))
            .with(p -> p.setSourceControl(sourceControl))
            .get();
    private final CreateProductDTO createProductDTO = Builder.build(CreateProductDTO.class)
            .with(p -> p.setName("New Product"))
            .with(p -> p.setGitlabGroupId(123))
            .with(p -> p.setSourceControlId(3L))
            .get();
    private final CreatePortfolioDTO createPortfolioDTO = Builder.build(CreatePortfolioDTO.class)
            .with(p -> p.setName("New Portfolio"))
            .with(p -> p.setGitlabGroupId(1234))
            .with(p -> p.setSourceControlId(3L))
            .get();

    @AfterEach
    public void tearDown() {
        clearRequestContext();
    }

    @Test
    public void should_validate_create_true() {
        when(productService.getAll()).thenReturn(List.of());
        when(portfolioService.getAll()).thenReturn(List.of());

        assertTrue(validator.isValid(createProductDTO, context));
        assertTrue(validator.isValid(createPortfolioDTO, context));
    }

    @Test
    public void should_validate_product_create_false() {
        CreateProductDTO newDTO = new CreateProductDTO();
        BeanUtils.copyProperties(createProductDTO, newDTO);
        newDTO.setGitlabGroupId(12345);

        EntityNotFoundException expectedProductError = new EntityNotFoundException(Product.class.getSimpleName(), 1L);

        when(productService.getAll()).thenReturn(List.of(foundProduct));
        when(productService.findByName(any())).thenThrow(expectedProductError);

        assertFalse(validator.isValid(createProductDTO, context));
        assertTrue(validator.isValid(newDTO, context));
    }

    @Test
    public void should_validate_portfolio_create_false() {
        CreatePortfolioDTO newDTO = new CreatePortfolioDTO();
        BeanUtils.copyProperties(createPortfolioDTO, newDTO);
        newDTO.setGitlabGroupId(12345);

        EntityNotFoundException expectedPortfolioError = new EntityNotFoundException(Portfolio.class.getSimpleName(), 1L);

        when(portfolioService.getAll()).thenReturn(List.of(foundPortfolio));
        when(portfolioService.findByName(any())).thenThrow(expectedPortfolioError);

        assertTrue(validator.isValid(newDTO, context));
        assertFalse(validator.isValid(createPortfolioDTO, context));
    }

    @Test
    public void should_validate_is_not_checking_against_itself() {
        Product product = new Product();
        BeanUtils.copyProperties(foundProduct, product);
        product.setId(4L);

        Portfolio portfolio = new Portfolio();
        BeanUtils.copyProperties(foundPortfolio, portfolio);
        portfolio.setId(5L);

        when(productService.getAll()).thenReturn(List.of(foundProduct));
        when(portfolioService.getAll()).thenReturn(List.of(foundPortfolio));

        when(productService.findByName(any())).thenReturn(product);
        when(portfolioService.findByName(any())).thenReturn(portfolio);

        assertFalse(validator.isValid(createProductDTO, context));
        assertFalse(validator.isValid(createPortfolioDTO, context));
    }

    @Test
    public void should_validate_true_when_source_control_is_null() {
        CreateProductDTO blankDTO = new CreateProductDTO();
        blankDTO.setName("Blank");
        blankDTO.setGitlabGroupId(345);

        assertTrue(validator.isValid(blankDTO, context));
    }

    @Test
    public void should_validate_true_when_gitlab_id_is_null() {
        CreateProductDTO blankDTO = new CreateProductDTO();
        blankDTO.setName("Blank");
        blankDTO.setSourceControlId(4L);

        assertTrue(validator.isValid(blankDTO, context));
    }

    @Test
    public void should_validate_true_when_found_product_gitlab_id_is_null() {
        Product newProduct = new Product();
        BeanUtils.copyProperties(foundProduct, newProduct);
        newProduct.setGitlabGroupId(null);

        when(productService.getAll()).thenReturn(List.of(newProduct));

        assertTrue(validator.isValid(createProductDTO, context));
    }

    @Test
    public void should_validate_true_when_found_product_source_control_is_null() {
        Product newProduct = new Product();
        BeanUtils.copyProperties(foundProduct, newProduct);
        newProduct.setSourceControl(null);

        when(productService.getAll()).thenReturn(List.of(newProduct));

        assertTrue(validator.isValid(createProductDTO, context));
    }

    @Test
    public void should_validate_true_when_found_portfolio_gitlab_id_is_null() {
        Portfolio newPortfolio = new Portfolio();
        BeanUtils.copyProperties(foundPortfolio, newPortfolio);
        newPortfolio.setGitlabGroupId(null);

        when(portfolioService.getAll()).thenReturn(List.of(newPortfolio));

        assertTrue(validator.isValid(createPortfolioDTO, context));
    }

    @Test
    public void should_validate_true_when_found_portfolio_source_control_is_null() {
        Portfolio newPortfolio = new Portfolio();
        BeanUtils.copyProperties(foundPortfolio, newPortfolio);
        newPortfolio.setSourceControl(null);

        when(portfolioService.getAll()).thenReturn(List.of(newPortfolio));

        assertTrue(validator.isValid(createPortfolioDTO, context));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "5 : 6",
            "5 : 3",
            "123 : 3",
            "123 : 6"
    }, delimiter = ':')
    void should_validate_is_duplicate_all_cases_for_product(Integer gitlabId, Long sourceControlId) {
        SourceControl sourceControl1 = new SourceControl();
        sourceControl1.setId(sourceControlId);

        Product newProduct = new Product();
        BeanUtils.copyProperties(foundProduct, newProduct);
        newProduct.setGitlabGroupId(gitlabId);
        newProduct.setSourceControl(sourceControl1);

        when(productService.getAll()).thenReturn(List.of(newProduct));
        when(productService.findByName(any())).thenReturn(foundProduct);

        assertTrue(validator.isValid(createProductDTO, context));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "5 : 6",
            "5 : 3",
            "1234 : 3",
            "1234 : 6"
    }, delimiter = ':')
    void should_validate_is_duplicate_all_cases_for_portfolio(Integer gitlabId, Long sourceControlId) {
        SourceControl sourceControl1 = new SourceControl();
        sourceControl1.setId(sourceControlId);

        Portfolio newPortfolio = new Portfolio();
        BeanUtils.copyProperties(foundPortfolio, newPortfolio);
        newPortfolio.setGitlabGroupId(gitlabId);
        newPortfolio.setSourceControl(sourceControl1);

        when(portfolioService.getAll()).thenReturn(List.of(newPortfolio));
        when(portfolioService.findByName(any())).thenReturn(foundPortfolio);

        assertTrue(validator.isValid(createPortfolioDTO, context));
    }

    private void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}
