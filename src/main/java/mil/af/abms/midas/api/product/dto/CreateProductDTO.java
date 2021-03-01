package mil.af.abms.midas.api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.product.validation.UniqueName;

@AllArgsConstructor
@Getter
public class CreateProductDTO {
    @UniqueName(isNew = true)
    String name;
    String description;
    Long gitlabProjectId;
}
