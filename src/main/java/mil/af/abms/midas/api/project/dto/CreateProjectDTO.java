package mil.af.abms.midas.api.project.dto;

import javax.validation.constraints.NotBlank;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.project.validation.UniqueName;
import mil.af.abms.midas.api.validation.TagsExist;
import mil.af.abms.midas.api.validation.TeamExists;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateProjectDTO {

    @NotBlank(message = "Project name must not be blank")
    @UniqueName(isNew = true)
    private String name;

    private Long gitlabProjectId;

    @TeamExists(allowNull = true)
    private Long teamId;

    @TagsExist
    private Set<Long> tagIds;

    private String description;

    private Long applicationId;

}