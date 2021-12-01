package mil.af.abms.midas.api.comment.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
import mil.af.abms.midas.api.measure.MeasureService;

public class IsAssertionOrMeasureCommentValidator implements ConstraintValidator<IsAssertionOrMeasureComment, CreateCommentDTO> {

    private AssertionService assertionService;
    private MeasureService measureService;

    @Autowired
    public void setAssertionService(AssertionService assertionService) { this.assertionService = assertionService; }

    @Autowired
    public void setMeasureService(MeasureService measureService) { this.measureService = measureService; }

    @Override
    public boolean isValid(CreateCommentDTO dto, ConstraintValidatorContext constraintContext) {
        constraintContext.disableDefaultConstraintViolation();
        if (dto.getAssertionId() != null && dto.getMeasureId() != null) {
            constraintContext.buildConstraintViolationWithTemplate("Comment must have only an assertion or measure").addConstraintViolation();
            return false;
        }
        else {
            constraintContext.buildConstraintViolationWithTemplate("No measure or assertion exists").addConstraintViolation();
            return assertionService.getById(dto.getAssertionId()).isPresent() || measureService.getById(dto.getMeasureId()).isPresent();
        }
    }
}
