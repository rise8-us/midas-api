package mil.af.abms.midas.api.personnel.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import mil.af.abms.midas.api.validation.UserExists;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonnelDTO implements PersonnelInterfaceDTO {

    @UserExists(allowNull = true)
    private Long ownerId;

    private Set<Long> teamIds;
    private Set<Long> adminIds;

}
