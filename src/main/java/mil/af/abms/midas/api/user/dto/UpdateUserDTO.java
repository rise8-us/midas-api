package mil.af.abms.midas.api.user.dto;

import java.io.Serializable;
import java.util.Set;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.user.validation.UniqueUsername;
import mil.af.abms.midas.api.validation.TeamsExist;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO implements Serializable {

    @ApiModelProperty(notes = "username must be unique")
    @UniqueUsername(isNew = false)
    private String username;

    @TeamsExist
    private Set<Long> teamIds;

    private String email;
    private String displayName;

}
