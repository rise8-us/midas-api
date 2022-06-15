package mil.af.abms.midas.api.product.dto;

import javax.validation.constraints.NotBlank;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import mil.af.abms.midas.api.personnel.dto.UpdatePersonnelDTO;
import mil.af.abms.midas.api.product.validation.UniqueProductName;
import mil.af.abms.midas.api.validation.ProjectsCanBeAssignedToProduct;
import mil.af.abms.midas.api.validation.ProjectsExist;
import mil.af.abms.midas.api.validation.TagsExist;
import mil.af.abms.midas.api.validation.UniqueGroupIdAndSourceControl;
import mil.af.abms.midas.enums.RoadmapType;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@UniqueGroupIdAndSourceControl
public class UpdateProductDTO implements ProductInterfaceDTO {

    @NotBlank(message = "product name must not be blank")
    @UniqueProductName(isNew = false)
    private String name;

    private String description;

    @ProjectsExist
    @ProjectsCanBeAssignedToProduct
    private Set<Long> projectIds;

    @TagsExist
    private Set<Long> tagIds;

    private Integer gitlabGroupId;
    private Long sourceControlId;
    private RoadmapType roadmapType;
    private UpdatePersonnelDTO personnel;
    private String vision;
    private String mission;
    private String problemStatement;

}
