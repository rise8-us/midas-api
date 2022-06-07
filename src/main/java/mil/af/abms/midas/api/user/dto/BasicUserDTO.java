package mil.af.abms.midas.api.user.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.UserType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicUserDTO implements AbstractDTO {
    private Long id;
    private String username;
    private String displayName;
    private UserType userType;
    private Set<Long> teamIds;
    private String email;
    private String phone;
    private String company;
}
