package mil.af.abms.midas.api.init.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import mil.af.abms.midas.api.announcement.dto.AnnouncementDTO;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.enums.Roles;

@Getter
public class InitDTO {
    public InitDTO(String classificationString, String caveat, UserDTO userDTO, List<AnnouncementDTO> announcementDTOs) {
        List<RoleDTO> rolesListDTO = new ArrayList<>();
        Roles.stream().forEach(r -> rolesListDTO.add(
                new RoleDTO(r.getName(), r.getOffset(), r.getDescription())
        ));
        this.roles = rolesListDTO;

        this.classification = new ClassificationDTO(classificationString, caveat);
        this.userLoggedIn = userDTO;
        this.unseenAnnouncements = announcementDTOs;

    }

    private final ClassificationDTO classification;
    private final List<RoleDTO> roles;
    private final UserDTO userLoggedIn;
    private final List<AnnouncementDTO> unseenAnnouncements;
}
