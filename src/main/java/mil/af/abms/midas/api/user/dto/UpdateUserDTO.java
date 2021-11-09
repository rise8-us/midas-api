package mil.af.abms.midas.api.user.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.user.validation.UniqueUsername;
import mil.af.abms.midas.api.validation.TeamsExist;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO implements Serializable {

    @UniqueUsername(isNew = false)
    private String username;

    @TeamsExist
    private Set<Long> teamIds = new HashSet<>();

    private String email;
    private String displayName;
    private String phone;
    private String company;

}
