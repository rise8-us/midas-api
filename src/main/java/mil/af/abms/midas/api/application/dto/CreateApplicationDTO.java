package mil.af.abms.midas.api.application.dto;

import javax.validation.constraints.NotBlank;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.application.validation.UniqueName;
import mil.af.abms.midas.api.validation.ProjectsExist;
import mil.af.abms.midas.api.validation.TagsExist;
import mil.af.abms.midas.api.validation.UserExists;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationDTO {

    @NotBlank(message = "application name must not be blank")
    @UniqueName(isNew = true)
    private String name;

    @UserExists(allowNull = true)
    private Long productManagerId;

    private String description;

    @ProjectsExist
    private Set<Long> projectIds;

    @TagsExist
    private Set<Long> tagIds;

}
