package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.clients.GitLab4JClient;

public class GitProjectExistsValidator implements ConstraintValidator<GitProjectExists, Integer> {

    @Autowired
    private GitLab4JClient gitLab4JClient;

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext constraintContext) {

        return gitLab4JClient.projectExistsById(id);
    }
}
