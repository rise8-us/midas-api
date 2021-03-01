package mil.af.abms.midas.api.team.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.team.validation.UniqueName;

@AllArgsConstructor
@Getter
public class CreateTeamDTO {
    @UniqueName(isNew = true)
    String name;
    Long gitlabGroupId;
}
