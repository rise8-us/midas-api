package mil.af.abms.midas.api.performancemeasure.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePerformanceMeasureDTO implements Serializable {

    @NotBlank(message = "title must not be empty")
    @NotNull(message = "title must not be null")
    private String title;

    @NotNull(message = "referenceId must not be null")
    private Integer referenceId;

    @NotNull(message = "capabilityId must not be null")
    private Long capabilityId;

}
