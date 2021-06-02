package mil.af.abms.midas.api.coverage.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.SonarQubeMaintainability;
import mil.af.abms.midas.enums.SonarQubeReliability;
import mil.af.abms.midas.enums.SonarQubeSecurity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoverageDTO implements AbstractDTO {

    private Long id;
    private Long projectId;
    private Integer jobId;
    private Float testCoverage;
    private Float coverageChange;
    private SonarQubeReliability reliabilityRating;
    private SonarQubeSecurity securityRating;
    private SonarQubeMaintainability maintainabilityRating;
    private LocalDateTime creationDate;
    private String pipelineUrl;
    private String triggeredBy;
    private String pipelineStatus;
    private String ref;

}
