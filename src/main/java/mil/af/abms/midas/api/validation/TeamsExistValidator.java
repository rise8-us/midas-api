package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.team.TeamService;

public class TeamsExistValidator implements ConstraintValidator<TeamsExist, Set<Long>> {

    @Autowired
    private TeamService teamService;

    @Override
    public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
        constraintContext.disableDefaultConstraintViolation();

        var violations = ids.stream().filter(i -> !teamService.existsById(i)).map(i -> {
                    constraintContext.buildConstraintViolationWithTemplate(
                            String.format("Team with id: %s does not exists", i)
                    ).addConstraintViolation();
                    return i;
                }
        ).collect(Collectors.toSet());

        return violations.isEmpty();
    }
    
}
