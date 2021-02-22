package mil.af.abms.midas.api.user.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import mil.af.abms.midas.api.helper.HttpPathVariableIdGrabber;
import mil.af.abms.midas.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.api.user.UserService;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private UserService userService;

    @Setter
    private boolean isNew;

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
        this.isNew = constraintAnnotation.isNew();
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintContext) {
        try {
            UserDTO existingUser = userService.findByUsername(username);
            if (isNew) {
                return false;
            } else {
                return HttpPathVariableIdGrabber.getPathId().equals(existingUser.getId());
            }
        } catch (EntityNotFoundException e) {
            return true;
        }
    }
}
