package mil.af.abms.midas.api.product.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.product.validation.GitProjectExists;
import mil.af.abms.midas.api.product.validation.UniqueName;

@AllArgsConstructor
@Getter
public class CreateProductDTO {

    @NotBlank(message = "Product name must not be blank")
    @UniqueName(isNew = true)
    String name;
    @GitProjectExists
    @NotNull(message = "Gitlab project ID must not be Null")
    Long gitlabProjectId;
    String description;
}
