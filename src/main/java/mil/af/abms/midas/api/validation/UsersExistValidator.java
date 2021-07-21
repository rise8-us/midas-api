package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.user.UserService;

public class UsersExistValidator implements ConstraintValidator<UsersExist, Set<Long>> {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
        constraintContext.disableDefaultConstraintViolation();

        var violations = ids.stream().filter(i -> !userService.existsById(i)).map(i ->
                constraintContext.buildConstraintViolationWithTemplate(
                        String.format("User with id: %s does not exists", i)
                ).addConstraintViolation()
        ).collect(Collectors.toSet());

        return violations.isEmpty();
    }
    
}
