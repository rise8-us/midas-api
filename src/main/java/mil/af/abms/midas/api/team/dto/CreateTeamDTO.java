package mil.af.abms.midas.api.team.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.team.validation.UniqueName;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateTeamDTO {
    @NotBlank(message = "Team name must not be blank")
    @UniqueName(isNew = true)
    String name;
    Long gitlabGroupId;
    String description;
}
