package mil.af.abms.midas.api.epic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
public class ProcessEpicsDTO implements AbstractDTO {

    private Double value;

}
