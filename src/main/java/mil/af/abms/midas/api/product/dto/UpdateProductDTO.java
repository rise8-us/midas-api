package mil.af.abms.midas.api.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.product.validation.UniqueName;

@Getter
@AllArgsConstructor
public class UpdateProductDTO {
    @ApiModelProperty(notes = "name must be unique")
    @UniqueName(isNew = false)
    private String name;
    private String description;
    private Boolean isArchived;
    private Long gitlabProjectId;
}
