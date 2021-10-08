package mil.af.abms.midas.api.deliverable.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliverableDTO implements Serializable {

    @NotBlank(message = "title must not be empty")
    @NotNull(message = "title must not be null")
    private String title;

    @NotNull(message = "referenceId must not be null")
    private Integer referenceId;

    private Integer index;
    private Long productId;
    private Long parentId;
    private List<CreateDeliverableDTO> children;
    private List<Long> releaseIds;
    private Long performanceMeasureId;
    private Long capabilityId;
    private Long assignedToId;
    private Long epicId;

}
