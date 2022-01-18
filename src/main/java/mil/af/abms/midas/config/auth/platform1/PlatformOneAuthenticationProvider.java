package mil.af.abms.midas.config.auth.platform1;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mil.af.abms.midas.api.appusermetrics.AppUserMetricsService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.Roles;

@Component
public class PlatformOneAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final AppUserMetricsService appUserMetricsService;

    @Autowired
    public PlatformOneAuthenticationProvider(UserService service, AppUserMetricsService appUserMetricsService) {
        this.userService = service;
        this.appUserMetricsService = appUserMetricsService;
    }

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LocalDate id = LocalDate.now();

        PlatformOneAuthenticationToken token = (PlatformOneAuthenticationToken) authentication;
        User user = userService.findByKeycloakUid(token.getKeycloakUid()).orElseGet(() -> userService.create(token));

        if (user.getLastLogin() == null || user.getLastLogin().isBefore(id.atStartOfDay())) {
            userService.updateLastLogin(user);
            appUserMetricsService.determineUpdateOrCreate(id);
        }

        List<GrantedAuthority> authorityList = new ArrayList<>(getRoles(user));
        authorityList.add(new SimpleGrantedAuthority("IS_AUTHENTICATED"));

        Authentication auth = new PlatformOneAuthenticationToken(user, null, authorityList);
        auth.setAuthenticated(true);
        return auth;
    }

    private List<GrantedAuthority> getRoles(User user) {

        List<GrantedAuthority> authorityList = new ArrayList<>();
        Map<Roles, Boolean> rolesMap = Roles.getRoles((user.getRoles()));
        rolesMap.forEach((role, hasRole) -> {
            if (Boolean.TRUE.equals(hasRole)) {
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getName());
                authorityList.add(simpleGrantedAuthority);
            }
        });

        return authorityList;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return PlatformOneAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
