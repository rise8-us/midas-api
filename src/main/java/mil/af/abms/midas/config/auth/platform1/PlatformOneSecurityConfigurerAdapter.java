package mil.af.abms.midas.config.auth.platform1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class PlatformOneSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private PlatformOneAuthenticationProvider platformOneAuthenticationProvider;

    @Value("${custom.environment}")
    private String env;
    @Value("${custom.localKeycloakUid}")
    private String localKeycloakUid;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var platformOneAuthenticationFilter = new PlatformOneAuthenticationFilter();

        if (env.equals("local")) {
            platformOneAuthenticationFilter.setLocalKeycloakUid(localKeycloakUid);
        }

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .anyRequest().hasAnyAuthority("IS_AUTHENTICATED").and()
                .addFilterBefore(platformOneAuthenticationFilter, BasicAuthenticationFilter.class)
                .csrf()
                .disable().headers().frameOptions();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(platformOneAuthenticationProvider);
    }

}
