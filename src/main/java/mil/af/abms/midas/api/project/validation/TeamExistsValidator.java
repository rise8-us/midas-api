package mil.af.abms.midas.api.project.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.team.TeamRepository;

public class TeamExistsValidator implements ConstraintValidator<TeamExists, Long> {

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {

        if (id == null) {
            return true;
        }

        return teamRepository.existsById(id);
    }
}
