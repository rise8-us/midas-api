package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.user.UserRepository;

public class UserExistsValidator implements ConstraintValidator<UserExists, Long> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {

        if (id == null) {
            return true;
        }

        return userRepository.existsById(id);
    }
}
