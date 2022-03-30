package mil.af.abms.midas.api.project.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.project.validation.UniqueProjectName;
import mil.af.abms.midas.api.validation.TagsExist;
import mil.af.abms.midas.api.validation.TeamExists;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectDTO implements Serializable {

    @NotBlank(message = "Project name must not be blank")
    @UniqueProjectName(isNew = false)
    private String name;

    private Integer gitlabProjectId;

    @TeamExists(allowNull = true)
    private Long teamId;

    @TagsExist
    private Set<Long> tagIds;

    private String description;
    private Long productId;
    private Long sourceControlId;

}
