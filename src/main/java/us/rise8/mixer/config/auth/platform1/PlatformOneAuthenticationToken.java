package us.rise8.mixer.config.auth.platform1;

import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlatformOneAuthenticationToken extends AbstractAuthenticationToken {

    private Long dodId;
    private Object principal;
    private Object credentials;
    private String keycloakUid;
    private String displayName;
    private String email;
    private List<String> groups;

    public PlatformOneAuthenticationToken(
            String keycloakUid,
            Long dodId,
            String username,
            String email,
            List<String> groups) {

        super(null);
        this.keycloakUid = keycloakUid;
        this.dodId = dodId;
        this.displayName = username;
        this.email = email;
        this.groups = groups;
    }

    public PlatformOneAuthenticationToken(Object principal, Object credentials,
                                          Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

}
