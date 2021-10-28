package mil.af.abms.midas.api.project.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.coverage.dto.CoverageDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO implements AbstractDTO {

    private Long id;
    private String name;
    private String description;
    private Boolean isArchived;
    private LocalDateTime creationDate;
    private Integer gitlabProjectId;
    private String webUrl;
    private Set<Long> tagIds =  new HashSet<>();
    private Long teamId;
    private Long projectJourneyMap;
    private Long productId;
    private CoverageDTO coverage;
    private Long sourceControlId;
    private Long ownerId;

}
