package mil.af.abms.midas.api.team.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.team.validation.UniqueName;
import mil.af.abms.midas.api.validation.UserExists;
import mil.af.abms.midas.api.validation.UsersExist;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTeamDTO implements Serializable {

    @ApiModelProperty(notes = "name must be unique")
    @NotBlank(message = "Team name must not be blank")
    @UniqueName(isNew = false)
    private String name;

    private Long gitlabGroupId;
    private String description;

    @UsersExist
    private Set<Long> userIds;
    @UserExists
    private Long productManagerId;
    @UserExists
    private Long designerId;
    @UserExists
    private Long techLeadId;
}
