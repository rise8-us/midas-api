package mil.af.abms.midas.api.user.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.team.TeamRepository;

public class TeamsExistValidator implements ConstraintValidator<TeamsExist, Set<Long>> {

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
        constraintContext.disableDefaultConstraintViolation();

        Set<Long> nonExistentIds = ids.stream().filter(i -> !teamRepository.existsById(i)).peek(i ->
                constraintContext.buildConstraintViolationWithTemplate(
                        String.format("Team with id: %s does not exists", i)
                ).addConstraintViolation()
        ).collect(Collectors.toSet());

        return nonExistentIds.isEmpty();
    }
}
