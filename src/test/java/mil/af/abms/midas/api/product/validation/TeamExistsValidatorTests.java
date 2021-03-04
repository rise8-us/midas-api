package mil.af.abms.midas.api.product.validation;

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

@ExtendWith(SpringExtension.class)
@Import({TeamExistsValidator.class})
public class TeamExistsValidatorTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final TeamEntity foundTeam = Builder.build(TeamEntity.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setCreationDate(CREATION_DATE))
            .with(t -> t.setIsArchived(false)).get();

    @Autowired
    TeamExistsValidator validator;
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
    public void should_Validate_Team_Exists_False() {
        when(teamService.findById(3L)).thenThrow(new EntityNotFoundException("Team"));

        assertFalse(validator.isValid(3L, context));
    }

    @Test
    public void should_Validate_Team_Exists_True() {
        when(teamService.findById(1L)).thenReturn(foundTeam);

        assertTrue(validator.isValid(1L, context));
    }

    private void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}
