package mil.af.abms.midas.api.project.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import mil.af.abms.midas.api.project.validation.TagExists;
import mil.af.abms.midas.api.project.validation.TeamExists;
import mil.af.abms.midas.api.project.validation.UniqueName;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectDTO {

    @NotBlank(message = "Project name must not be blank")
    @UniqueName(isNew = false)
    private String name;
    @NotNull(message = "Gitlab project ID must not be Null")
    private Long gitlabProjectId;
    @TeamExists
    private Long teamId;
    @TagExists
    private Set<Long> tagIds;
    private String description;
    private Boolean isArchived;

}
