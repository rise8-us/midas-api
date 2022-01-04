package mil.af.abms.midas.api.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.enums.ProgressionStatus;

@ExtendWith(SpringExtension.class)
@Import({IsValidDueDateValidator.class})
public class IsValidDueDateValidatorTests {

    @Autowired
    IsValidDueDateValidator validator;
    @MockBean
    private AssertionService assertionService;
    @MockBean
    private UpdateAssertionDTO dto;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    private final LocalDate today = LocalDate.now();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setText("foo"))
            .with(a -> a.setStartDate(today))
            .with(a -> a.setDueDate(null))
            .with(a -> a.setStatus(ProgressionStatus.ON_TRACK)).get();
    UpdateAssertionDTO assertionDTOWithValidDueDate = new UpdateAssertionDTO("updated", ProgressionStatus.ON_TRACK, List.of(), null, false, today.toString(), today.plusDays(1L).toString());
    UpdateAssertionDTO assertionDTOWithInValidDueDate = new UpdateAssertionDTO("updated", ProgressionStatus.ON_TRACK, List.of(), null, false, today.toString(), today.minusDays(1L).toString());
    UpdateAssertionDTO assertionDTOWithoutStartDateWithDueDate = new UpdateAssertionDTO("updated", ProgressionStatus.ON_TRACK, List.of(), null, false, null, today.toString());

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_validate_due_date_true() {
        Assertion updatedAssertion = new Assertion();
        BeanUtils.copyProperties(assertion, updatedAssertion);
        when(assertionService.updateById(1L, assertionDTOWithValidDueDate)).thenReturn(updatedAssertion);

        assertTrue(validator.isValid(assertionDTOWithValidDueDate, context));
    }

    @Test
    public void should_validate_false_when_due_date_is_more_recent_than_start_date() {
        Assertion updatedAssertion = new Assertion();
        BeanUtils.copyProperties(assertion, updatedAssertion);
        when(assertionService.updateById(1L, assertionDTOWithInValidDueDate)).thenReturn(updatedAssertion);

        assertFalse(validator.isValid(assertionDTOWithInValidDueDate, context));
    }

    @Test
    public void should_validate_false_when_populating_due_date_prior_to_start_date() {
        Assertion updatedAssertion = new Assertion();
        BeanUtils.copyProperties(assertion, updatedAssertion);
        when(assertionService.updateById(1L, assertionDTOWithoutStartDateWithDueDate)).thenReturn(updatedAssertion);

        assertFalse(validator.isValid(assertionDTOWithoutStartDateWithDueDate, context));
    }

}
