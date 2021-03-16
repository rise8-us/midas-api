package mil.af.abms.midas.api.team.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.team.validation.UniqueName;

@Getter
@AllArgsConstructor
public class UpdateTeamDTO {
    @ApiModelProperty(notes = "name must be unique")
    @UniqueName(isNew = false)
    private String name;
    private Long gitlabGroupId;
    private String description;
}
