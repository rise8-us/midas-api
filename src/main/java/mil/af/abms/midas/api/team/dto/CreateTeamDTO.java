package mil.af.abms.midas.api.team.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.team.validation.UniqueName;
import mil.af.abms.midas.api.validation.UserExists;
import mil.af.abms.midas.api.validation.UsersExist;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateTeamDTO implements Serializable {

    @NotBlank(message = "Team name must not be blank")
    @UniqueName(isNew = true)
    private String name;

    private Long gitlabGroupId;
    private String description;

    @UsersExist
    private Set<Long> userIds;
    @UserExists(message = "The Product Manger must be a valid MIDAS user", allowNull = true)
    private Long productManagerId;
    @UserExists(message = "The Designer must be a valid MIDAS user", allowNull = true)
    private Long designerId;
    @UserExists(message = "The Tech Lead must be a valid MIDAS user", allowNull = true)
    private Long techLeadId;
}
