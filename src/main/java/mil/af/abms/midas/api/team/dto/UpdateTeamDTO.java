package mil.af.abms.midas.api.team.dto;

import javax.validation.constraints.NotBlank;

import java.util.Set;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.team.validation.UniqueName;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTeamDTO {
    @ApiModelProperty(notes = "name must be unique")
    @NotBlank(message = "Team name must not be blank")
    @UniqueName(isNew = false)
    private String name;
    private Long gitlabGroupId;
    private String description;
    Set<Long> userIds;
}
