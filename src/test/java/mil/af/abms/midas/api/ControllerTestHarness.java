package mil.af.abms.midas.api;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.metrics.appusermetrics.AppUserMetricsService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.config.auth.platform1.PlatformOneAuthenticationProvider;
import mil.af.abms.midas.config.auth.platform1.PlatformOneSecurityConfigurerAdapter;

@ExtendWith(SpringExtension.class)
@Import({PlatformOneAuthenticationProvider.class, PlatformOneSecurityConfigurerAdapter.class})
public abstract class ControllerTestHarness {

    @Autowired
    protected PlatformOneAuthenticationProvider authenticationProvider;
    @Autowired
    protected PlatformOneSecurityConfigurerAdapter securityConfigurerAdapter;
    @Autowired
    protected MockMvc mockMvc;
    @MockBean
    protected UserService userService;
    @MockBean
    protected AppUserMetricsService appUserMetricsService;

    protected ObjectMapper mapper = JsonMapper.dateMapper();

    protected User authUser = Builder.build(User.class)
            .with(u -> u.setRoles(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("grogu"))
            .with(u -> u.setLastLogin(LocalDateTime.now()))
            .get();
}
