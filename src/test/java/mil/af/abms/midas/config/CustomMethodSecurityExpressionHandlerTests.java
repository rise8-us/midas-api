package mil.af.abms.midas.config;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.config.security.CustomMethodSecurityExpressionHandler;
import mil.af.abms.midas.config.security.CustomMethodSecurityExpressionRoot;

@ExtendWith(SpringExtension.class)
@Import({CustomMethodSecurityExpressionHandler.class, CustomMethodSecurityExpressionRoot.class, SpringContext.class})
public class CustomMethodSecurityExpressionHandlerTests {

    @Autowired
    CustomMethodSecurityExpressionHandler handler;
    @MockBean
    Authentication auth;
    @MockBean
    MethodInvocation methodInvocation;

    @Test
    public void should_create_root() throws Exception {
        Class<?> clazz = CustomMethodSecurityExpressionHandler.class;
        Method method = clazz.getDeclaredMethod("createSecurityExpressionRoot" , Authentication.class, MethodInvocation.class);
        method.setAccessible(true);

        CustomMethodSecurityExpressionRoot root = (CustomMethodSecurityExpressionRoot) method.invoke(handler, auth, methodInvocation);

        assertFalse(root.hasTeamAccess(null));
    }

}
