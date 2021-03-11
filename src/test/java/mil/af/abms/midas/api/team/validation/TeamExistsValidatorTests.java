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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.api.product.validation.TeamExistsValidator;
import mil.af.abms.midas.api.team.TeamRepository;

@ExtendWith(SpringExtension.class)
@Import({TeamExistsValidator.class})
public class TeamExistsValidatorTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();

    @Autowired
    TeamExistsValidator validator;
    @MockBean
    private TeamRepository teamRepository;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_validate_team_exists_true() {
        when(teamRepository.existsById(1L)).thenReturn(true);

        assertTrue(validator.isValid(1L, context));
    }

    @Test
    public void should_validate_team_exists_false() {
        when(teamRepository.existsById(10L)).thenReturn(false);

        assertFalse(validator.isValid(1L, context));
    }
}
