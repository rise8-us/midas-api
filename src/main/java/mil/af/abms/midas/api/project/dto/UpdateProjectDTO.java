package mil.af.abms.midas.api.project.dto;

import javax.validation.constraints.NotBlank;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import mil.af.abms.midas.api.project.validation.UniqueName;
import mil.af.abms.midas.api.validation.TagsExist;
import mil.af.abms.midas.api.validation.TeamExists;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectDTO {

    @NotBlank(message = "Project name must not be blank")
    @UniqueName(isNew = false)
    private String name;

    private Long gitlabProjectId;

    @TeamExists(allowNull = true)
    private Long teamId;

    @TagsExist
    private Set<Long> tagIds;

    private String description;
    private Long productId;

}
