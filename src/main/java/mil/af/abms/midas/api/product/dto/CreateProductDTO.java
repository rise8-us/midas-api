package mil.af.abms.midas.api.product.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.product.validation.UniqueName;
import mil.af.abms.midas.api.validation.ProductExists;
import mil.af.abms.midas.api.validation.ProductsExist;
import mil.af.abms.midas.api.validation.ProjectsCanBeAssignedToProduct;
import mil.af.abms.midas.api.validation.ProjectsExist;
import mil.af.abms.midas.api.validation.TagsExist;
import mil.af.abms.midas.api.validation.UserExists;
import mil.af.abms.midas.enums.ProductType;
import mil.af.abms.midas.enums.RoadmapType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDTO implements Serializable {

    @NotBlank(message = "name must not be blank")
    @UniqueName(isNew = true)
    private String name;

    private String description;

    @UserExists(allowNull = true)
    private Long ownerId;

    @ProductExists
    private Long parentId;

    @ProjectsExist
    @ProjectsCanBeAssignedToProduct
    private Set<Long> projectIds;

    @TagsExist
    private Set<Long> tagIds;

    @ProductsExist
    private Set<Long> childIds;

    private ProductType type;
    private Integer gitlabGroupId;
    private Long sourceControlId;
    private Set<Long> teamIds;
    private String vision;
    private String mission;
    private String problemStatement;
    private RoadmapType roadmapType;

}
