package mil.af.abms.midas.api.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.SyncStatus;

@Data
@NoArgsConstructor
public class PaginationProgressDTO implements AbstractDTO {

    private Double value;
    private Long id;
    private SyncStatus status = SyncStatus.SYNCING;

}
