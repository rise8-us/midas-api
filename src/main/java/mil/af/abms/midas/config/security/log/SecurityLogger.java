package mil.af.abms.midas.config.security.log;

import org.springframework.context.event.EventListener;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SecurityLogger {

    @EventListener
    public void authenticated(AuthenticationSuccessEvent event) {
        String userName = event.getAuthentication().getName();
        log.debug(String.format("MIDAS: Successful login - [username: %s]", userName));
    }

    @EventListener
    public void authorized(AuthorizedEvent event) {
        String userName = event.getAuthentication().getName();
        log.debug(String.format("MIDAS: Authorization successful [username: %s]", userName));
    }

    @EventListener
    public void authenticationFailure(AbstractAuthenticationFailureEvent event) {
        String userName = event.getAuthentication().getName();
        String source = RequestParser.getRemoteAddress(((FilterInvocation) event.getSource()).getRequest());
        log.error(String.format("MIDAS: Failed login  [source: \"%s\" username: \"%s\"]", source, userName));
    }

    @EventListener
    public void authorizationFailure(AuthorizationFailureEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        String message = event.getAccessDeniedException().getMessage();
        try {
            String source = RequestParser.getRemoteAddress(((FilterInvocation) event.getSource()).getRequest());
            log.error(String.format("MIDAS: Unauthorized access - [source: \"%s\" username: \"%s\", message: \"%s\"]", source, principal, message));
        } catch (ClassCastException e) {
            log.error("MIDAS: Unauthorized websocket access");
        }
    }

}
