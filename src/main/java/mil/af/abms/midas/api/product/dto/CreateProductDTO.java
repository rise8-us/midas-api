package mil.af.abms.midas.api.product.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mil.af.abms.midas.api.product.validation.UniqueName;

@AllArgsConstructor
@Getter
public class CreateProductDTO {

    @NotBlank
    @UniqueName(isNew = true)
    String name;
    @NotNull
    Long gitlabProjectId;
    String description;
}
