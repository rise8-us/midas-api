package us.rise8.mixer.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.rise8.mixer.api.user.UserModel;
import us.rise8.mixer.api.user.UserRepository;
import us.rise8.mixer.api.user.UserService;

@Component
public class Startup {

    private static final Logger LOG = LoggerFactory.getLogger(Startup.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomProperty property;

    @PostConstruct
    public void init() {
        LOG.info("ENVIRONMENT: " + property.getEnvironment());
        if (!property.getEnvironment().equalsIgnoreCase("local")) {
            UserModel rootUser = userService.getObject(1L);

            if (Boolean.FALSE.equals(rootUser.getIsDisabled())) {
                LOG.info("DISABLING ROOT USER");
                rootUser.setIsDisabled(true);
                userRepository.save(rootUser);
                LOG.info("DISABLED ROOT USER");
            }
        }
    }
}
