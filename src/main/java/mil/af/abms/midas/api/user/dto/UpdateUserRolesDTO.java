package mil.af.abms.midas.api.user.dto;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

import lombok.Data;

@Data
public class UpdateUserRolesDTO implements Serializable {
    @NotNull
    private Long roles;
}
