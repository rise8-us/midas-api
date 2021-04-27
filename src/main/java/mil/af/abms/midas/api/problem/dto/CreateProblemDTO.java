package mil.af.abms.midas.api.problem.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProblemDTO {

    @NotBlank(message = "Problem must not be blank")
    private String problem;
    private Long productId;
    private Long portfolioId;

}
