package mil.af.abms.midas.api.project.validation;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamRepository;

@ExtendWith(SpringExtension.class)
@Import({TeamExistsValidator.class})
public class TeamExistsValidatorTests {

    @Autowired
    TeamExistsValidator validator;
    @MockBean
    private TeamRepository teamRepository;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final Team foundTeam = Builder.build(Team.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("MIDAS"))
            .with(t -> t.setCreationDate(CREATION_DATE))
            .with(t -> t.setIsArchived(false)).get();

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_validate_team_exists_false() {
        when(teamRepository.existsById(3L)).thenReturn(false);

        assertFalse(validator.isValid(3L, context));
    }

    @Test
    public void should_validate_team_exists_true() {
        when(teamRepository.existsById(1L)).thenReturn(true);

        assertTrue(validator.isValid(1L, context));
    }

    @Test
    public void should_validate_team_exists_true_when_null() {
        when(teamRepository.existsById(null)).thenReturn(true);

        assertTrue(validator.isValid(null, context));
    }
}
