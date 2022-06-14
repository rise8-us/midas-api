package mil.af.abms.midas.api.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
public class PaginationProgressDTO implements AbstractDTO {

    private Double value;

}
