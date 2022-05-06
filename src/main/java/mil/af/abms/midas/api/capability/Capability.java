package mil.af.abms.midas.api.capability;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.DeliverableInterface;
import mil.af.abms.midas.api.capability.dto.CapabilityDTO;
import mil.af.abms.midas.api.deliverable.Deliverable;
import mil.af.abms.midas.api.missionthread.MissionThread;
import mil.af.abms.midas.api.performancemeasure.PerformanceMeasure;
import mil.af.abms.midas.api.portfolio.Portfolio;

@Entity @Setter @Getter
@Table(name = "capability")
public class Capability extends AbstractEntity<CapabilityDTO> implements DeliverableInterface {

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "INT")
    private Integer referenceId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "capability")
    private Set<PerformanceMeasure> performanceMeasures = new HashSet<>();

    @OneToMany
    @JoinTable(
            name = "capability_deliverable",
            joinColumns = @JoinColumn(name = "capability_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "deliverable_id", referencedColumnName = "id"))
    private Set<Deliverable> deliverables = new HashSet<>();

    @ManyToOne
    @JoinTable(
            name = "capability_mission_thread",
            joinColumns = @JoinColumn(name = "capability_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "mission_thread_id", referencedColumnName = "id"))
    private MissionThread missionThread;

    @ManyToOne
    @JoinTable(
            name = "portfolio_capability",
            joinColumns = @JoinColumn(name = "capability_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"))
    private Portfolio portfolio;

    public CapabilityDTO toDto() {
        return new CapabilityDTO(
                id,
                creationDate,
                title,
                description,
                getIds(performanceMeasures),
                referenceId,
                getIdOrNull(missionThread),
                getIds(deliverables),
                isArchived,
                getIdOrNull(portfolio)
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
        Capability that = (Capability) o;
        return this.hashCode() == that.hashCode();
    }
}
