package mil.af.abms.midas.api.application.dto;

import javax.validation.constraints.NotBlank;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.application.validation.UniqueName;
import mil.af.abms.midas.api.validation.UserExists;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateApplicationDTO {

    @NotBlank(message = "application name must not be blank")
    @UniqueName(isNew = false)
    private String name;
    @UserExists
    private Long productManagerId;
    private String description;
    private Set<Long> projectIds;
    private Set<Long> tagIds;

}
