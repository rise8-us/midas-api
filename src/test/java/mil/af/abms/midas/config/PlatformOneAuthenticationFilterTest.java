package mil.af.abms.midas.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationFilter;
import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationToken;
import mil.af.abms.midas.helpers.MockJWT;

@ExtendWith(SpringExtension.class)
public class PlatformOneAuthenticationFilterTest {

    PlatformOneAuthenticationFilter filter = new PlatformOneAuthenticationFilter();

    @Test
    public void should_get_bearer_from_request_and_not_throw() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer " + MockJWT.get(true));

        filter.doFilterInternal(request, response, filterChain);

    }

    @Test
    public void should_get_bearer_from_request_skip_dodIdStr_and_not_throw() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer " + MockJWT.get(false));

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

    @Test
    public void should_set_all_required_fields_for_token() {
        PlatformOneAuthenticationToken token = new PlatformOneAuthenticationToken(null, null, null, null, null);
        token.setKeycloakUid("123-abc");
        token.setDodId(1L);
        token.setEmail("abc@123.4");
        token.setGroups(List.of("hello"));
        token.setDisplayName("jeff");

        assertThat(token.getKeycloakUid()).isEqualTo("123-abc");
        assertThat(token.getDodId()).isEqualTo(1L);
        assertThat(token.getEmail()).isEqualTo("abc@123.4");
        assertThat(token.getGroups()).isEqualTo(List.of("hello"));
        assertThat(token.getDisplayName()).isEqualTo("jeff");
    }

    @Test
    public void should_return_empty_string_getClaimsKeyAsString() throws Exception {
        Class<?> clazz = PlatformOneAuthenticationFilter.class;
        Method method = clazz.getDeclaredMethod("getClaimsKeyAsString", Map.class, String.class);
        method.setAccessible(true);

        assertThat(method.invoke(filter, new HashMap<>(), "foo")).isEqualTo("");
    }

    @Test
    public void should_return_empty_string_getClaimsKeyAsList() throws Exception {
        Class<?> clazz = PlatformOneAuthenticationFilter.class;
        Method method = clazz.getDeclaredMethod("getClaimsKeyAsList", Map.class, String.class);
        method.setAccessible(true);

        assertThat(method.invoke(filter, new HashMap<>(), "foo")).isEqualTo(new ArrayList<>());
    }

    @Test
    public void should_get_and_set() {
        filter.setLocalKeycloakUid("fizzBang");
        assertThat(filter.getLocalKeycloakUid()).isEqualTo("fizzBang");
    }

    @Test
    public void should_set_and_get_token() {
        PlatformOneAuthenticationToken token = new PlatformOneAuthenticationToken(null, null, null, null, null);
        token.setCredentials("fizz");
        token.setPrincipal("bang");

        assertThat((String) token.getCredentials()).isEqualTo("fizz");
        assertThat((String) token.getPrincipal()).isEqualTo("bang");
    }

}
