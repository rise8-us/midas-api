package mil.af.abms.midas.config.security.log;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationToken;
import mil.af.abms.midas.helpers.MemoryAppender;

class SecurityLoggerTests {

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @SpyBean
    SecurityLogger logger;

    @Mock
    RequestParser parser;

    private final MemoryAppender memoryAppender = new MemoryAppender();

    private final User user = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("Hello")).get();

    private final List<GrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority("IS_AUTHENTICATED"));
    private final Authentication auth = new PlatformOneAuthenticationToken(user, null, authorityList);

    @BeforeEach
    public void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(SecurityLogger.class.getName());
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @Test
    void log_abstract_failure() {
        runner.withBean(SecurityLogger.class).run(context -> {
                context.publishEvent(new AuthenticationFailureBadCredentialsEvent(auth, new AuthenticationCredentialsNotFoundException("not")));
            assertThat(memoryAppender.countEventsForLogger(SecurityLogger.class.getName())).isEqualTo(2);
        });
    }

    @Test
    void log_auth_failure() {
        runner.withBean(SecurityLogger.class).run(context -> {
                context.publishEvent(new AuthorizationFailureEvent(new Object(), List.of(), auth, new AccessDeniedException("denied")));
            assertThat(memoryAppender.countEventsForLogger(SecurityLogger.class.getName())).isEqualTo(2);
        });
    }

    @Test
    void log_authorized_event_success() {
        runner.withBean(SecurityLogger.class).run(context -> {
                context.publishEvent(new AuthorizedEvent(new Object(), List.of(), auth));
            assertThat(memoryAppender.countEventsForLogger(SecurityLogger.class.getName())).isEqualTo(1);
        });
    }

}
