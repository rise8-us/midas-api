package mil.af.abms.midas.api.helper;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

public final class HttpPathVariableIdGrabber {

    private HttpPathVariableIdGrabber() {
        throw new IllegalStateException("Utility Class");
    }

    public static Long getPathId() {
        return getPathIdByName("id");
    }

    public static Long getPathIdByName(String varName) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())
        ).getRequest();
        @SuppressWarnings("unchecked")
        Map<String, String> variables = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
        );
        if (variables.get(varName) == null) {
            return null;
        }
        return Long.valueOf(variables.get(varName));
    }

}
