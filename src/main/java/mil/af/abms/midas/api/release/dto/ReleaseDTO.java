package mil.af.abms.midas.api.release.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseDTO implements AbstractDTO {

    private Long id;
    private String title;
    private LocalDateTime creationDate;
    private LocalDateTime targetDate;
    private ProgressionStatus status;
    private Set<Long> deliverableIds;
    private Boolean isArchived;

}
