package mil.af.abms.midas.api.dtos;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

import mil.af.abms.midas.api.validation.GitLabEpicExistsForProduct;

@Data
@AllArgsConstructor
@GitLabEpicExistsForProduct
public class AddGitLabEpicWithProductDTO {

    @NotNull(message = "iId cannot be null")
    private Integer iId;

    @NotNull(message = "productId cannot be null")
    private Long productId;

}
