package mil.af.abms.midas.api.coverage.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.SonarqubeMaintainability;
import mil.af.abms.midas.enums.SonarqubeReliability;
import mil.af.abms.midas.enums.SonarqubeSecurity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoverageDTO implements AbstractDTO {

    private Long id;
    private Long projectId;
    private Integer jobId;
    private Float testCoverage;
    private Float coverageChange;
    private SonarqubeReliability reliabilityRating;
    private SonarqubeSecurity securityRating;
    private SonarqubeMaintainability maintainabilityRating;
    private LocalDateTime creationDate;
    private String pipelineUrl;
    private String triggeredBy;
    private String pipelineStatus;
    private String ref;

}
