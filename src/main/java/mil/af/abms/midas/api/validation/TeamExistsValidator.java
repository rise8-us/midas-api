package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.team.TeamService;

public class TeamExistsValidator implements ConstraintValidator<TeamExists, Long> {

    @Autowired
    private TeamService teamService;

    @Setter
    private boolean allowNull;

    @Override
    public void initialize(TeamExists constraintAnnotation) { this.allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {

        return id == null ? allowNull : teamService.existsById(id);
    }
}
