package us.rise8.mixer.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import us.rise8.mixer.config.auth.platform1.PlatformOneAuthenticationFilter;
import us.rise8.mixer.helpers.MockJWT;

@ExtendWith(SpringExtension.class)
public class PlatformOneAuthenticationFilterTest {

    PlatformOneAuthenticationFilter filter = new PlatformOneAuthenticationFilter();

    @Test
    public void shouldGetBearerFromRequestAndNotThrow() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer " + MockJWT.get());

        filter.doFilterInternal(request, response, filterChain);

    }

    @Test
    public void shouldThrowWhenNoKeycloakSub() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        assertThrows(AuthenticationCredentialsNotFoundException.class, () ->
                filter.doFilterInternal(request, response, filterChain));
    }

    @Test
    public void shouldThrowWhenBearerEmpty() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer ");

        assertThrows(AuthenticationCredentialsNotFoundException.class, () ->
                filter.doFilterInternal(request, response, filterChain));
    }

    @Test
    public void shouldThrowWhenBearerJWTInvalid() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer " + MockJWT.encode("foo".getBytes()));

        AuthenticationCredentialsNotFoundException e = assertThrows(AuthenticationCredentialsNotFoundException.class, () ->
                filter.doFilterInternal(request, response, filterChain));

        assertThat(e.getMessage()).isEqualTo("Invalid JWT");
    }
}
