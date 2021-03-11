package mil.af.abms.midas.api.team.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import mil.af.abms.midas.api.team.TeamEntity;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.exception.EntityNotFoundException;
import mil.af.abms.midas.helpers.RequestContext;

@ExtendWith(SpringExtension.class)
@Import({UniqueNameValidator.class})
public class UniqueNameValidatorTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final TeamEntity foundTeam = Builder.build(TeamEntity.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("foo"))
            .with(t -> t.setCreationDate(CREATION_DATE)).get();

    @Autowired
    UniqueNameValidator validator;
    @MockBean
    private TeamService teamService;
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
    public void should_validate_new_team_true() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(true);

        when(teamService.findByName("foo")).thenThrow(new EntityNotFoundException("Team"));

        assertTrue(validator.isValid(foundTeam.getName(), context));
    }

    @Test
    public void should_validate_new_team_false() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(true);

        when(teamService.findByName("foo")).thenReturn(foundTeam);

        assertFalse(validator.isValid(foundTeam.getName(), context));
    }

    @Test
    public void should_validate_update_team_true() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(false);

        when(teamService.findByName("foo")).thenReturn(foundTeam);

        assertTrue(validator.isValid(foundTeam.getName(), context));
    }

    @Test
    public void should_validate_update_team_false() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(false);

        when(teamService.findByName("foo")).thenReturn(foundTeam);

        assertFalse(validator.isValid(foundTeam.getName(), context));
    }

    private void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}
