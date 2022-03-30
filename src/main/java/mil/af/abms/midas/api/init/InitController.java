package mil.af.abms.midas.api.init;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.announcement.Announcement;
import mil.af.abms.midas.api.announcement.AnnouncementService;
import mil.af.abms.midas.api.init.dto.InitDTO;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.config.CustomProperty;

@RestController
@RequestMapping("/init")
public class InitController {

    private final CustomProperty property;
    private final UserService userService;
    private final AnnouncementService announcementService;

    @Autowired
    public InitController(
            CustomProperty property, UserService userService, AnnouncementService announcementService
    ) {
        this.userService = userService;
        this.property = property;
        this.announcementService = announcementService;
    }

    @GetMapping
    public InitDTO getInfo(Authentication auth) {
        var userDTO = userService.getUserBySecContext().toDto();
        var announcementDTOs = announcementService.getUnseenAnnouncements(userService.getUserBySecContext())
                .stream().map(Announcement::toDto).collect(Collectors.toList());
        return new InitDTO(property.getClassification(), property.getCaveat(), userDTO, announcementDTOs);
    }
}
