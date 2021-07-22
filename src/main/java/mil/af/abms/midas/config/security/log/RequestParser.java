package mil.af.abms.midas.config.security.log;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestParser {

    private static final String USER = "Anonymous";
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private static final String AUTHORIZATION = "Authorization";

    private RequestParser() {
        throw new IllegalStateException("Utility Class");
    }

    public static String getUser(HttpServletRequest request) {
        try {
            String accessToken = request.getHeader(AUTHORIZATION).substring(7);
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
        return USER;
    }

    public static String getRemoteAddress(HttpServletRequest request) {
        String ipFromHeader = Optional.ofNullable(request.getHeader(X_FORWARDED_FOR)).orElse("");
        if (!ipFromHeader.isEmpty()) {
            log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }

}
