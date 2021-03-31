package mil.af.abms.midas.api.product.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.clients.GitLab4JClient;

public class GitProjectExistsValidator implements ConstraintValidator<GitProjectExists, Long> {

    @Autowired
    private GitLab4JClient gitLab4JClient;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {

        return gitLab4JClient.projectExistsById(id);
    }
}
