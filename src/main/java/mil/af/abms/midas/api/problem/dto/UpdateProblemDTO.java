package mil.af.abms.midas.api.problem.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProblemDTO {

    @NotBlank(message = "Problem must not be blank")
    private String problem;
    private Long productId;

}
