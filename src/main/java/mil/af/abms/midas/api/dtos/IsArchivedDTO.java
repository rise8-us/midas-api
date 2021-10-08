package mil.af.abms.midas.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsArchivedDTO implements AbstractDTO {

    private Boolean isArchived;

}
