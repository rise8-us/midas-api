package mil.af.abms.midas.api.team.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.helper.HttpPathVariableIdGrabber;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.exception.EntityNotFoundException;

public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {

    @Autowired
    private TeamService teamService;

    @Setter
    private boolean isNew;

    @Override
    public void initialize(UniqueName constraintAnnotation) {
        this.isNew = constraintAnnotation.isNew();
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintContext) {
        try {
            Team existingTeam = teamService.findByName(name);
            if (isNew) {
                return false;
            } else {
                return HttpPathVariableIdGrabber.getPathId().equals(existingTeam.getId());
            }
        } catch (EntityNotFoundException e) {
            return true;
        }
    }
}
