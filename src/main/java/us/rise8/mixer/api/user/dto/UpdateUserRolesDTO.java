package us.rise8.mixer.api.user.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateUserRolesDTO {
    @NotNull
    private Long roles;
}
