package mil.af.abms.midas.api.user.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateUserRolesDTO {
    @NotNull
    private Long roles;
}
