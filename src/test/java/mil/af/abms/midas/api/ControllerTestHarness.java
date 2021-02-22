package mil.af.abms.midas.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationProvider;
import mil.af.abms.midas.config.auth.platform1.PlatformOneSecurityConfigurerAdapter;

@TestPropertySource(properties = {"custom.environment=local", "custom.localKeycloakUid"})
@ExtendWith(SpringExtension.class)
@Import({PlatformOneAuthenticationProvider.class, PlatformOneSecurityConfigurerAdapter.class})
public abstract class ControllerTestHarness {

    @Autowired
    protected PlatformOneAuthenticationProvider authenticationProvider;
    @Autowired
    protected PlatformOneSecurityConfigurerAdapter securityConfigurerAdapter;
}
