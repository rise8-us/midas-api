package mil.af.abms.midas.api.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.clients.GitLab4JClient;

@ExtendWith(SpringExtension.class)
@Import({GitProjectExistsValidator.class})
public class GitProjectExistsValidatorTests {

    @Autowired
    GitProjectExistsValidator validator;
    @MockBean
    private GitLab4JClient gitLab4JClient;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_validate_project_exists_false() {
        when(gitLab4JClient.projectExistsById(3)).thenReturn(false);

        assertFalse(validator.isValid(3, context));
    }

    @Test
    public void should_validate_project_exists_true() {
        when(gitLab4JClient.projectExistsById(1)).thenReturn(true);

        assertTrue(validator.isValid(1, context));
    }

}
