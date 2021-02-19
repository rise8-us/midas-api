package us.rise8.mixer.api.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import us.rise8.mixer.api.AbstractDTO;

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
    private Boolean isDisabled;
    private Long roles;
}
