package mil.af.abms.midas.api.capability.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCapabilityDTO implements CapabilityInterfaceDTO {

    @NotBlank(message = "title must not be empty")
    @NotNull(message = "title must not be null")
    private String title;

    private String description;

    @NotNull(message = "referenceId must not be null")
    private Integer referenceId;

    private Long portfolioId;

}
