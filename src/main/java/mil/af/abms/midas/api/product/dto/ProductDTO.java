package mil.af.abms.midas.api.product.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;
import mil.af.abms.midas.api.release.dto.ReleaseDTO;
import mil.af.abms.midas.api.tag.dto.TagDTO;
import mil.af.abms.midas.enums.RoadmapType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements AbstractDTO {

    private Long id;
    private String name;
    private String description;
    private PersonnelDTO personnel;
    private Boolean isArchived;
    private LocalDateTime creationDate;
    private Set<Long> projectIds;
    private Set<TagDTO> tags;
    private Integer gitlabGroupId;
    private Long sourceControlId;
    private String vision;
    private String mission;
    private String problemStatement;
    private RoadmapType roadmapType;
    private Long portfolioId;
    private ReleaseDTO latestRelease;

}
