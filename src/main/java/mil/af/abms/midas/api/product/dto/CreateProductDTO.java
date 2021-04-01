package mil.af.abms.midas.api.product.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.product.validation.UniqueName;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateProductDTO {

    @NotBlank(message = "Product name must not be blank")
    @UniqueName(isNew = true)
    String name;
    @NotNull(message = "Gitlab project ID must not be Null")
    Long gitlabProjectId;
    String description;
}
