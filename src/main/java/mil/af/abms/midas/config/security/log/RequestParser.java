package mil.af.abms.midas.config.security.log;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
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
            return Optional.ofNullable(request.getHeader(AUTHORIZATION)).map(a -> a.substring(7)).map(accessToken -> {
                var decodedJWT = JWT.decode(accessToken);
                var claims = decodedJWT.getClaims();
                return Optional.ofNullable(claims).map(c -> c.get("sub").asString()).orElse(USER);
            }).orElse(USER);
        } catch (JWTDecodeException e) {
                log.warn("Invalid JWT");
                return USER;
            }
    }

    public static String getRemoteAddress(HttpServletRequest request) {
        String ipFromHeader = Optional.ofNullable(request.getHeader(X_FORWARDED_FOR)).orElse("");
        if (!ipFromHeader.isEmpty()) {
            log.info("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }

}
