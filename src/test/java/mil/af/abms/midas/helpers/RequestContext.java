package mil.af.abms.midas.helpers;

import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

public class RequestContext {

    public static void setRequestContext(String key, String value) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of(key, value));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
