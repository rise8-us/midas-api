package mil.af.abms.midas.api.init;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import mil.af.abms.midas.api.announcement.AnnouncementEntity;
import mil.af.abms.midas.api.announcement.AnnouncementService;
import mil.af.abms.midas.api.announcement.dto.AnnouncementDTO;
import mil.af.abms.midas.api.init.dto.InfoDTO;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.config.CustomProperty;

@RestController
@RequestMapping("/init")
@Api(tags = "FrontEnd initialization")
public class InitController {

    private final CustomProperty property;
    private final UserService userService;
    private final AnnouncementService announcementService;

    @Autowired
    public InitController(CustomProperty property, UserService userService, AnnouncementService announcementService) {
        this.userService = userService;
        this.property = property;
        this.announcementService = announcementService;
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

    @ApiOperation(value = "returns unseen announcements",
            notes = "Looks for unseen announcements since user last login")
    @GetMapping("/announcements")
    public List<AnnouncementDTO> getUnseenAnnouncement(Authentication auth) {

        return announcementService.getUnseenAnnouncements(userService.getUserFromAuth(auth))
                .stream().map(AnnouncementEntity::toDto).collect(Collectors.toList());
    }
}
