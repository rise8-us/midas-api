package mil.af.abms.midas.api.problem.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.validation.ProductExists;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProblemDTO {

    @NotBlank(message = "text must not be blank")
    private String text;

    @ProductExists
    private Long productId;

}
