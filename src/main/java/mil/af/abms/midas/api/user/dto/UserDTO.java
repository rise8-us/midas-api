package mil.af.abms.midas.api.user.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.UserType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements AbstractDTO {
    private Long id;
    private String keycloakUid;
    private String username;
    private String email;
    private String displayName;
    private LocalDateTime creationDate;
    private Long dodId;
    private UserType userType;
    private Boolean isDisabled;
    private Long roles;
    private LocalDateTime lastLogin;
    private Set<Long> teamIds;
    private String phone;
    private String company;
}
