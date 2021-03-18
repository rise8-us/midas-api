package mil.af.abms.midas.api.product.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.product.validation.TeamExists;
import mil.af.abms.midas.api.product.validation.UniqueName;

@Getter
@AllArgsConstructor
public class UpdateProductDTO {

    @NotBlank(message = "Product name must not be blank")
    @UniqueName(isNew = false)
    private final String name;
    @NotNull(message = "Gitlab project ID must not be Null")
    private final Long gitlabProjectId;
    @TeamExists
    private final Long teamId;
    private final String description;
    private final Boolean isArchived;

}
