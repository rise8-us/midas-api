package mil.af.abms.midas.api.tag.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.helper.HttpPathVariableIdGrabber;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.exception.EntityNotFoundException;

public class UniqueLabelValidator implements ConstraintValidator<UniqueLabel, String> {

    @Autowired
    private TagService tagService;

    @Setter
    private boolean isNew;

    @Override
    public void initialize(UniqueLabel constraintAnnotation) {
        this.isNew = constraintAnnotation.isNew();
    }

    @Override
    public boolean isValid(String label, ConstraintValidatorContext constraintContext) {
        try {
            Tag existingTag = tagService.findByLabel(label);
            if (isNew) {
                return false;
            } else {
                return HttpPathVariableIdGrabber.getPathId().equals(existingTag.getId());
            }
        } catch (EntityNotFoundException e) {
            return true;
        }
    }
}
