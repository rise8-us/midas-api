package mil.af.abms.midas.api.helper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationToken;

public class JsonMapperTests {

    private final User user = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setKeycloakUid("Hello")).get();

    @Test
    public void should_throw_error_if_private_constructor_is_called() throws Exception {
        Class<?> clazz = JsonMapper.class;
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertThat(exception.getCause().getClass()).isEqualTo(IllegalStateException.class);
    }

    @Test
    public void should_get_keycloak_uid_from_auth() throws AuthenticationCredentialsNotFoundException {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("IS_AUTHENTICATED"));
        Authentication auth = new PlatformOneAuthenticationToken(user, null, authorityList);

        assertThat(JsonMapper.getKeycloakUidFromAuth(auth)).isEqualTo("Hello");
    }

    @Test public void should_build_conditions_map() throws IOException {
        String resourceName = "src/test/resources/condition.json";
        String conditionStr = Files.readString(Path.of(resourceName));
        InputStream stream = new ByteArrayInputStream(conditionStr.getBytes(StandardCharsets.UTF_8));
        Map<String, String> conditions = JsonMapper.getConditions(stream);

        assertThat(conditions.entrySet().size()).isEqualTo(8);
        assertThat(conditions.get("coverage")).isEqualTo("88.2");
    }

}
