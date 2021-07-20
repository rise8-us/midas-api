package mil.af.abms.midas.config.security.log;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestParser {

    private RequestParser() {
        throw new IllegalStateException("Utility Class");
    }

    public static String getUser(HttpServletRequest request) {
        String user = "Anonymous";
        if (!request.getRequestURI().startsWith("/actuator") || !request.getRequestURI().endsWith("public")) {
            try {
                String accessToken = request.getHeader("Authorization").substring(7);
                DecodedJWT decodedJWT = JWT.decode(accessToken);
                Map<String, Claim> claims = decodedJWT.getClaims();
                return claims.get("sub").asString();
            } catch (NullPointerException e) {
                log.warn("No Authorization header present: {} {} {}", request.getMethod(), request.getRequestURI(),
                        getRemoteAddress(request));
            } catch (Exception e) {
                log.warn("INVALID JWT: {} {} {}", request.getMethod(), request.getRequestURI(),
                        getRemoteAddress(request));
            }
        }

        return user;
    }

    public static String getRemoteAddress(HttpServletRequest request) {
        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if (ipFromHeader != null && ipFromHeader.length() > 0) {
            log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }

}
