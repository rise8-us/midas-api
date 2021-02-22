package mil.af.abms.midas.api.init;

import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.config.CustomProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import mil.af.abms.midas.api.init.dto.InfoDTO;

@RestController
@RequestMapping("/init")
@Api(tags = "FrontEnd initialization")
public class InitController {

    private final CustomProperty property;
    private final UserService userService;

    @Autowired
    public InitController(CustomProperty property, UserService userService) {
        this.userService = userService;
        this.property = property;
    }

    @ApiOperation(value = "context info",
            notes = "Returns classification context, as well as available roles a user may have")
    @GetMapping("/info")
    public InfoDTO getInfo() {
        return new InfoDTO(property.getClassification(), property.getCaveat());
    }

    @ApiOperation(value = "Returns currently logged in user",
            notes = "PlatformOne identity management never informs the frontend of the user identity")
    @GetMapping("/user")
    public UserDTO loginInit(Authentication auth) throws JsonProcessingException {
        return userService.getUserFromAuth(auth).toDto();
    }
}
