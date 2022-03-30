package mil.af.abms.midas.api.project.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.exception.EntityNotFoundException;
import mil.af.abms.midas.helpers.RequestContext;

@ExtendWith(SpringExtension.class)
@Import({UniqueProjectNameValidator.class})
public class UniqueProjectNameValidatorTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final Project foundProject = Builder.build(Project.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setDescription("MIDAS Project"))
            .with(p -> p.setGitlabProjectId(2))
            .with(p -> p.setCreationDate(CREATION_DATE))
            .with(p -> p.setIsArchived(false)).get();

    @Autowired
    UniqueProjectNameValidator validator;
    @MockBean
    private ProjectService projectService;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @AfterEach
    public void tearDown() {
        clearRequestContext();
    }

    @Test
    public void should_validate_new_project_true() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(true);

        when(projectService.findByName("MIDAS")).thenThrow(new EntityNotFoundException("Project"));

        assertTrue(validator.isValid(foundProject.getName(), context));
    }

    @Test
    public void should_validate_new_project_false() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(true);

        when(projectService.findByName("MIDAS")).thenReturn(foundProject);

        assertFalse(validator.isValid(foundProject.getName(), context));
    }

    @Test
    public void should_validate_update_project_true() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(false);

        when(projectService.findByName(any())).thenReturn(foundProject);

        assertTrue(validator.isValid(foundProject.getName(), context));
    }

    @Test
    public void should_validate_update_project_false() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(false);

        when(projectService.findByName(any())).thenReturn(foundProject);

        assertFalse(validator.isValid(foundProject.getName(), context));
    }

    private void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}
