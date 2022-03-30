package mil.af.abms.midas.api.personnel.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelDTO implements AbstractDTO {

    private Long id;
    private Long ownerId;
    private Set<Long> teamIds;
    private Set<Long> adminIds;

}
