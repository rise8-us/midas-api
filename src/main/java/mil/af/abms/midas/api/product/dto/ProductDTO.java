package mil.af.abms.midas.api.product.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.tag.dto.TagDTO;
import mil.af.abms.midas.enums.ProductType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements AbstractDTO {

    private Long id;
    private Long productManagerId;
    private Long parentId;
    private String name;
    private String description;
    private Boolean isArchived;
    private LocalDateTime creationDate;
    private Set<Long> projectIds;
    private Set<TagDTO> tags;
    private Set<Long> children;
    private ProductType type;
    private Integer gitlabGroupId;
    private Long gitlabConfigId;

}
