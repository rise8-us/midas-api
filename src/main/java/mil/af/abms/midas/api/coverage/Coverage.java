package mil.af.abms.midas.api.coverage;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.coverage.dto.CoverageDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.enums.SonarQubeMaintainability;
import mil.af.abms.midas.enums.SonarQubeReliability;
import mil.af.abms.midas.enums.SonarQubeSecurity;

@Entity @Setter @Getter
@Table(name = "coverage")
public class Coverage extends AbstractEntity<CoverageDTO> {

    private Float testCoverage = 0F;
    private Float coverageChange = 0F;
    private Integer jobId = -1;

    @Enumerated(EnumType.STRING)
    private SonarQubeReliability reliabilityRating = SonarQubeReliability.U;

    @Enumerated(EnumType.STRING)
    private SonarQubeSecurity securityRating = SonarQubeSecurity.U;

    @Enumerated(EnumType.STRING)
    private SonarQubeMaintainability maintainabilityRating = SonarQubeMaintainability.U;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Transient
    private String pipelineUrl;
    @Transient
    private String triggeredBy;
    @Transient
    private String pipelineStatus;
    @Transient
    private String ref;

    @Override
    public CoverageDTO toDto() {
        return new CoverageDTO(
                id,
                getIdOrNull(project),
                jobId,
                testCoverage,
                coverageChange,
                reliabilityRating,
                securityRating,
                maintainabilityRating,
                creationDate,
                pipelineUrl,
                triggeredBy,
                pipelineStatus,
                ref
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coverage that = (Coverage) o;
        return this.hashCode() == that.hashCode();
    }


}
