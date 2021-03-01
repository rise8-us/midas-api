package mil.af.abms.midas.api.product.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.exception.EntityNotFoundException;

public class TeamExistsValidator implements ConstraintValidator<TeamExists, Long> {

    @Autowired
    private TeamService teamService;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {
        try {
            teamService.findById(id);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}
