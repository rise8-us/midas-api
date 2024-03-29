package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.user.UserService;

public class UserExistsValidator implements ConstraintValidator<UserExists, Long> {

    @Autowired
    private UserService userService;

    @Setter
    private boolean allowNull;

    @Override
    public void initialize(UserExists constraintAnnotation) { this.allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {
        return id == null ? allowNull : userService.existsById(id);
    }
}
