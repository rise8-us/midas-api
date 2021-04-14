package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.tag.TagService;

public class TagsExistValidator implements ConstraintValidator<TagsExist, Set<Long>> {

    @Autowired
    private TagService tagService;

    @Override
    public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
        constraintContext.disableDefaultConstraintViolation();

        Set<Long> nonExistentIds = ids.stream().filter(i -> !tagService.existsById(i)).peek(i ->
                constraintContext.buildConstraintViolationWithTemplate(
                        String.format("Tag with id: %s does not exists", i)
                ).addConstraintViolation()
        ).collect(Collectors.toSet());

        return nonExistentIds.isEmpty();
    }
}
