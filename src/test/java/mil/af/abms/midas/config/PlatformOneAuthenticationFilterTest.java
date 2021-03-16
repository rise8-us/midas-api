package mil.af.abms.midas.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationFilter;
import mil.af.abms.midas.helpers.MockJWT;

@ExtendWith(SpringExtension.class)
public class PlatformOneAuthenticationFilterTest {

    PlatformOneAuthenticationFilter filter = new PlatformOneAuthenticationFilter();

    @Test
    public void should_get_bearer_from_request_and_not_throw() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer " + MockJWT.get());

        filter.doFilterInternal(request, response, filterChain);

    }

    @Test
    public void should_throw_when_no_keycloak_sub() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        assertThrows(AuthenticationCredentialsNotFoundException.class, () ->
                filter.doFilterInternal(request, response, filterChain));
    }

    @Test
    public void should_throw_when_bearer_empty() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer ");

        assertThrows(AuthenticationCredentialsNotFoundException.class, () ->
                filter.doFilterInternal(request, response, filterChain));
    }

    @Test
    public void should_throw_when_bearer_jwt_invalid() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer " + MockJWT.encode("foo".getBytes()));

        AuthenticationCredentialsNotFoundException e = assertThrows(AuthenticationCredentialsNotFoundException.class, () ->
                filter.doFilterInternal(request, response, filterChain));

        assertThat(e.getMessage()).isEqualTo("Invalid JWT");
    }
}
