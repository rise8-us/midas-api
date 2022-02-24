package mil.af.abms.midas.api.deliverable.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.completion.dto.UpdateCompletionDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDeliverableDTO implements Serializable {

    private Long id;

    @NotBlank(message = "title must not be empty")
    @NotNull(message = "title must not be null")
    private String title;

    @NotNull(message = "referenceId must not be null")
    private Integer referenceId;

    private Integer index;
    private List<Long> releaseIds;
    private ProgressionStatus status;
    private Long assignedToId;
    
    private UpdateCompletionDTO completion;

}
