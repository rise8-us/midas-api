package mil.af.abms.midas.api.validation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;

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

    private final CreateProductDTO createProductDTO = Builder.build(CreateProductDTO.class)
            .with(p -> p.setName("New Product"))
            .with(p -> p.setGitlabGroupId(123))
            .with(p -> p.setSourceControlId(3L))
            .get();

    @AfterEach
    public void tearDown() {
        clearRequestContext();
    }

    @ParameterizedTest
    @CsvSource(value = {
            " : 1",
            "345 : ",
    }, delimiter = ':')
    public void should_validate_true_when_gitlab_id_is_null(Integer gitlabGroupId, Long sourceControlId) {
        CreateProductDTO blankDTO = new CreateProductDTO();
        blankDTO.setName("Blank");
        blankDTO.setSourceControlId(sourceControlId);
        blankDTO.setGitlabGroupId(gitlabGroupId);

        assertTrue(validator.isValid(blankDTO, context));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "true : true : true",
            "true : false : false",
            "false : false : false",
            "false : true : false"
    }, delimiter = ':')
    void should_validate_is_duplicate_all_cases_for_product(boolean productDuplicate, boolean portfolioDuplicate, boolean expected) {
        when(productService.validateUniqueSourceControlAndGitlabGroup(any())).thenReturn(productDuplicate);
        when(portfolioService.validateUniqueSourceControlAndGitlabGroup(any())).thenReturn(portfolioDuplicate);

        assertThat(validator.isValid(createProductDTO, context)).isEqualTo(expected);
    }

    private void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}
