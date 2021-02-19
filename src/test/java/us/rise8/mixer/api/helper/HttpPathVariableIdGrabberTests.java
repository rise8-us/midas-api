package us.rise8.mixer.api.helper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import us.rise8.mixer.helpers.RequestContext;

@ExtendWith(SpringExtension.class)
public class HttpPathVariableIdGrabberTests {

    @Test
    public void shouldThrowErrorIfPrivateConstructorIsCalled() throws Exception {
        Class<?> clazz = HttpPathVariableIdGrabber.class;
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertThat(exception.getCause().getClass()).isEqualTo(IllegalStateException.class);
    }

    @Test
    public void shouldReturnPathId() {
        RequestContext.setRequestContext("id", "1");
        assertThat(HttpPathVariableIdGrabber.getPathId()).isEqualTo(1L);

    }


}
