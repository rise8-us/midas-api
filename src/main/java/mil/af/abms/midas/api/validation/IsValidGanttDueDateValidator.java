package mil.af.abms.midas.api.validation;

import static mil.af.abms.midas.api.helper.TimeConversion.getLocalDateOrNullFromObject;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import mil.af.abms.midas.api.gantt.GanttDateInterfaceDTO;

public class IsValidGanttDueDateValidator implements ConstraintValidator<IsValidGanttDueDate, GanttDateInterfaceDTO> {

    @Override
    public boolean isValid(GanttDateInterfaceDTO dto, ConstraintValidatorContext constraintContext) {

        constraintContext.disableDefaultConstraintViolation();
        var start = getLocalDateOrNullFromObject(dto.getStartDate());
        var due = getLocalDateOrNullFromObject(dto.getDueDate());

        if (start != null && due != null && start.isAfter(due)) {
            constraintContext.buildConstraintViolationWithTemplate("Due date must be after start date").addConstraintViolation();
            return false;
        }

        return true;
    }
}
