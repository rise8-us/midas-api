package mil.af.abms.midas.api.metrics.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMetricsPageViewDTO {

    @NotBlank(message = "Pathname must not be blank")
    @NotNull(message = "A pathname must be provided")
    private String pathname;
}
