package mil.af.abms.midas.api.validation;

import static mil.af.abms.midas.api.helper.TimeConversion.getLocalDateOrNullFromObject;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import mil.af.abms.midas.api.dtos.CompletableDTO;

public class IsValidDueDateValidator implements ConstraintValidator<IsValidDueDate, CompletableDTO> {

    @Override
    public boolean isValid(CompletableDTO dto, ConstraintValidatorContext constraintContext) {

        constraintContext.disableDefaultConstraintViolation();
        var start = getLocalDateOrNullFromObject(dto.getStartDate());
        var due = getLocalDateOrNullFromObject(dto.getDueDate());

        if (due != null && start == null) {
            constraintContext.buildConstraintViolationWithTemplate("Start date must be set prior to due date").addConstraintViolation();
            return false;
        }

        if (due != null && start.isAfter(due)) {
            constraintContext.buildConstraintViolationWithTemplate("Due date must be after start date").addConstraintViolation();
            return false;
        }

        return true;
    }
}
