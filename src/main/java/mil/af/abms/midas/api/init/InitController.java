package mil.af.abms.midas.api.init;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import mil.af.abms.midas.api.announcement.Announcement;
import mil.af.abms.midas.api.announcement.AnnouncementService;
import mil.af.abms.midas.api.announcement.dto.AnnouncementDTO;
import mil.af.abms.midas.api.init.dto.InitDTO;
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
            notes = "Returns classification context, available roles a user may have, user login, and unseen announcements")
    @GetMapping
    public InitDTO getInfo(Authentication auth) {
        UserDTO userDTO = userService.getUserFromAuth(auth).toDto();
        List<AnnouncementDTO> announcementDTOs = announcementService.getUnseenAnnouncements(userService.getUserFromAuth(auth))
                .stream().map(Announcement::toDto).collect(Collectors.toList());
        return new InitDTO(property.getClassification(), property.getCaveat(), userDTO, announcementDTOs);
    }
}
