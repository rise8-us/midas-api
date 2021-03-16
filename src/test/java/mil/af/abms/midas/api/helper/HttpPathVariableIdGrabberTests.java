package mil.af.abms.midas.api.helper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.helpers.RequestContext;

@ExtendWith(SpringExtension.class)
public class HttpPathVariableIdGrabberTests {

    @Test
    public void should_throw_error_if_private_constructor_is_called() throws Exception {
        Class<?> clazz = HttpPathVariableIdGrabber.class;
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertThat(exception.getCause().getClass()).isEqualTo(IllegalStateException.class);
    }

    @Test
    public void should_return_path_id() {
        RequestContext.setRequestContext("id", "1");
        assertThat(HttpPathVariableIdGrabber.getPathId()).isEqualTo(1L);

    }


}
