package mil.af.abms.midas.api.product.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.product.validation.UniqueName;
import mil.af.abms.midas.api.validation.ProjectsCanBeAssignedToProduct;
import mil.af.abms.midas.api.validation.ProjectsExist;
import mil.af.abms.midas.api.validation.TagsExist;
import mil.af.abms.midas.api.validation.UserExists;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductDTO implements Serializable {

    @NotBlank(message = "product name must not be blank")
    @UniqueName(isNew = false)
    private String name;

    private String description;

    @UserExists(allowNull = true)
    private Long productManagerId;

    private Long parentId;

    @ProjectsExist
    @ProjectsCanBeAssignedToProduct
    private Set<Long> projectIds;

    @TagsExist
    private Set<Long> tagIds;

}