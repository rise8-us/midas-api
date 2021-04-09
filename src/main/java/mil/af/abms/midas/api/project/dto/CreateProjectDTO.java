package mil.af.abms.midas.api.project.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.project.validation.UniqueName;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateProjectDTO {

    @NotBlank(message = "Project name must not be blank")
    @UniqueName(isNew = true)
    String name;
    @NotNull(message = "Gitlab project ID must not be Null")
    Long gitlabProjectId;
    String description;
}
