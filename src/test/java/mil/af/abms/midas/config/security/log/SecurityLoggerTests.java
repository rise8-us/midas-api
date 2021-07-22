package mil.af.abms.midas.config.security.log;

import java.util.List;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationToken;

class SecurityLoggerTests {

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @SpyBean
    SecurityLogger logger;

    @Mock
    RequestParser parser;

    private final User user = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("Hello")).get();

    private final List<GrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority("IS_AUTHENTICATED"));
    private final Authentication auth = new PlatformOneAuthenticationToken(user, null, authorityList);


    @Test
    void log_auth_success() {
        runner.withBean(SecurityLogger.class).run(context -> {
                context.publishEvent(new AuthenticationSuccessEvent(auth));
        });
    }

    @Test
    void log_abstract_failure() {
        runner.withBean(SecurityLogger.class).run(context -> {
                context.publishEvent(new AuthenticationFailureBadCredentialsEvent(auth, new AuthenticationCredentialsNotFoundException("not")));
        });
    }

    @Test
    void log_auth_failure() {
        runner.withBean(SecurityLogger.class).run(context -> {
                context.publishEvent(new AuthorizationFailureEvent(new Object(), List.of(), auth, new AccessDeniedException("denied")));
        });
    }

    @Test
    void log_authorized_event_success() {
        runner.withBean(SecurityLogger.class).run(context -> {
                context.publishEvent(new AuthorizedEvent(new Object(), List.of(), auth));
        });
    }

}
