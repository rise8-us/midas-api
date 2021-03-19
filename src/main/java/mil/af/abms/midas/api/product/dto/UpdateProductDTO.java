package mil.af.abms.midas.api.product.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import mil.af.abms.midas.api.product.validation.TeamExists;
import mil.af.abms.midas.api.product.validation.UniqueName;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDTO {

    @NotBlank(message = "Product name must not be blank")
    @UniqueName(isNew = false)
    private String name;
    @NotNull(message = "Gitlab project ID must not be Null")
    private Long gitlabProjectId;
    @TeamExists
    private Long teamId;
    private String description;
    private Boolean isArchived;

}
