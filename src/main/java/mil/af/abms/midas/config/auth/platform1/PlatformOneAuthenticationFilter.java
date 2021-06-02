package mil.af.abms.midas.config.auth.platform1;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlatformOneAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final Long NO_DODID = -1L;
    /**
     * Used only for local Development when a user is needed and
     * Istio/Envoy isn't available to add a Bearer Token this is
     * only populated if .envrc ENVIRONMENT=local
     */
    @Setter @Getter
    private String localKeycloakUid;

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        String dodIdStr = null;
        String displayName = null;
        String email = null;
        String keycloakUid = null;
        String cert;
        List<String> groups = new ArrayList<>();

        if (authorizationHeader != null) {
            try {
                String[] authArray = authorizationHeader.split("Bearer ");
                if (authArray.length != 2)
                    throw new AuthenticationCredentialsNotFoundException("Unable to locate Authorization Token");

                String token = authArray[1];
                DecodedJWT decodedJWT = JWT.decode(token);
                Map<String, Claim> claims = decodedJWT.getClaims();

                keycloakUid = getClaimsKeyAsString(claims, "sub");
                cert = getClaimsKeyAsString(claims, "usercertificate");
                displayName = getClaimsKeyAsString(claims, "name");
                email = getClaimsKeyAsString(claims, "email");
                List<String> allGroups = getClaimsKeyAsList(claims, "group-simple");
                groups = allGroups.stream().filter(g -> g.matches("^midas.*")).collect(Collectors.toList());

                String[] certSplit = cert.split("\\.");

                if (certSplit.length >= 3) {
                    dodIdStr = certSplit[certSplit.length - 1];
                }

            } catch (JWTDecodeException e) {
                throw new AuthenticationCredentialsNotFoundException("Invalid JWT");
            }
        }

        if (localKeycloakUid == null && keycloakUid == null) {
            throw new AuthenticationCredentialsNotFoundException("no keycloak sub provided");
        }

        Long dodId = dodIdStr == null ? NO_DODID : Long.valueOf(dodIdStr);
        keycloakUid = keycloakUid == null ? localKeycloakUid : keycloakUid;
        Authentication authentication = new PlatformOneAuthenticationToken(keycloakUid, dodId, displayName, email, groups);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    protected String getClaimsKeyAsString(Map<String, Claim> claims, String key) {
        try {
            return claims.get(key).asString();
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            return "";
        }
    }

    protected List<String> getClaimsKeyAsList(Map<String, Claim> claims, String key) {
        try {
            return claims.get(key).asList(String.class);
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }
}


