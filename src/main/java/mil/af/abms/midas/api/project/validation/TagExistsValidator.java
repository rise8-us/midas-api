package mil.af.abms.midas.api.project.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.tag.TagRepository;

public class TagExistsValidator implements ConstraintValidator<TagExists, Set<Long>> {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
        constraintContext.disableDefaultConstraintViolation();

        Set<Long> nonExistentIds = ids.stream().filter(i -> !tagRepository.existsById(i)).peek(i ->
                constraintContext.buildConstraintViolationWithTemplate(
                        String.format("Tag with id: %s does not exists", i)
                ).addConstraintViolation()
        ).collect(Collectors.toSet());

        return nonExistentIds.isEmpty();
    }
}
