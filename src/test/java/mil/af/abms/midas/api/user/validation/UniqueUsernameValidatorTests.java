package mil.af.abms.midas.api.user.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.exception.EntityNotFoundException;
import mil.af.abms.midas.helpers.RequestContext;
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

@ExtendWith(SpringExtension.class)
@Import({UniqueUsernameValidator.class})
public class UniqueUsernameValidatorTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final UserDTO foundUser = Builder.build(UserDTO.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("foo"))
            .with(u -> u.setEmail("a.b@c"))
            .with(u -> u.setDisplayName("BigFoo"))
            .with(u -> u.setCreationDate(CREATION_DATE))
            .with(u -> u.setRoles(0L)).get();
    @Autowired
    UniqueUsernameValidator validator;
    @MockBean
    private UserService userService;
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
    public void shouldValidateNewUserTrue() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(true);

        when(userService.findByUsername(any())).thenThrow(new EntityNotFoundException("User"));

        assertTrue(validator.isValid(foundUser.getUsername(), context));
    }

    @Test
    public void shouldValidateNewUserFalse() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(true);

        when(userService.findByUsername(any())).thenReturn(foundUser);

        assertFalse(validator.isValid(foundUser.getUsername(), context));
    }

    @Test
    public void shouldValidateUpdateUserTrue() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(false);

        when(userService.findByUsername(any())).thenReturn(foundUser);

        assertTrue(validator.isValid(foundUser.getUsername(), context));
    }

    @Test
    public void shouldValidateUpdateUserFalse() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(false);

        when(userService.findByUsername(any())).thenReturn(foundUser);

        assertFalse(validator.isValid(foundUser.getUsername(), context));
    }

    private void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}
