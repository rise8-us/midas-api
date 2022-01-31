package mil.af.abms.midas.api.performancemeasure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.DeliverableInterface;
import mil.af.abms.midas.api.capability.Capability;
import mil.af.abms.midas.api.deliverable.Deliverable;
import mil.af.abms.midas.api.performancemeasure.dto.PerformanceMeasureDTO;

@Entity @Setter @Getter
@Table(name = "performance_measure")
public class PerformanceMeasure extends AbstractEntity<PerformanceMeasureDTO> implements DeliverableInterface {

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "INT")
    private Integer referenceId;

    @OneToMany(mappedBy = "performanceMeasure", orphanRemoval = true)
    private Set<Deliverable> deliverables = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capability_id", nullable = true)
    private Capability capability;

    public PerformanceMeasureDTO toDto() {
        return new PerformanceMeasureDTO(
                id,
                title,
                creationDate,
                getIds(deliverables),
                referenceId,
                getIdOrNull(capability),
                isArchived
        );
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerformanceMeasure that = (PerformanceMeasure) o;
        return this.hashCode() == that.hashCode();
    }
}
