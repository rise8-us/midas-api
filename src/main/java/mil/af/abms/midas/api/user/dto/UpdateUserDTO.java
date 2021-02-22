package mil.af.abms.midas.api.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.user.validation.UniqueUsername;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    @ApiModelProperty(notes = "username must be unique")
    @UniqueUsername(isNew = false)
    private String username;
    private String email;
    private String displayName;
}
