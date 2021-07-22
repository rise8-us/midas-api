package mil.af.abms.midas.config.security.log;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.springframework.mock.web.MockHttpServletRequest;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.helpers.MockJWT;

class RequestParserTests {

    @Test
    void should_throw_error_if_private_constructor_is_called() throws Exception {
        Class<?> clazz = RequestParser.class;
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertThat(exception.getCause().getClass()).isEqualTo(IllegalStateException.class);
    }

    @Test
    void should_getUser() {
        var request = new MockHttpServletRequest();

        request.addHeader("Authorization", "Bearer " + MockJWT.get(true));

        assertThat(RequestParser.getUser(request)).isEqualTo("abc-123");
    }

    @Test
    void should_catch_null_pointer_getUser() {
        var request = new MockHttpServletRequest();

        assertThat(RequestParser.getUser(request)).isEqualTo("Anonymous");
    }

    @Test
    void should_catch_exception_getUser() {
        var request = new MockHttpServletRequest();

        request.addHeader("Authorization", "Bearer ");

        assertThat(RequestParser.getUser(request)).isEqualTo("Anonymous");
    }

    @Test
    void should_return_x_forwarded_address() {
        var request = new MockHttpServletRequest();

        request.addHeader("X-FORWARDED-FOR", "1.2.3.4");

        assertThat(RequestParser.getRemoteAddress(request)).isEqualTo("1.2.3.4");
    }

    @Test
    void should_return_address() {
        var request = new MockHttpServletRequest();

        request.setRemoteAddr("1.2.3.4");

        assertThat(RequestParser.getRemoteAddress(request)).isEqualTo("1.2.3.4");
    }

}
