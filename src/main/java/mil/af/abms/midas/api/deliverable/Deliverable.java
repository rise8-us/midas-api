package mil.af.abms.midas.api.deliverable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.capability.Capability;
import mil.af.abms.midas.api.completion.Completion;
import mil.af.abms.midas.api.deliverable.dto.DeliverableDTO;
import mil.af.abms.midas.api.gantt.target.Target;
import mil.af.abms.midas.api.performancemeasure.PerformanceMeasure;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.ProgressionStatus;

@Entity @Setter @Getter
@Table(name = "deliverable")
public class Deliverable extends AbstractEntity<DeliverableDTO> {

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "INT")
    private Integer position;

    @Column(columnDefinition = "INT")
    private Integer referenceId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'NOT_STARTED'", nullable = false)
    private ProgressionStatus status = ProgressionStatus.NOT_STARTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Deliverable parent;

    @OneToMany(mappedBy = "parent")
    private Set<Deliverable> children = new HashSet<>();

    @ManyToOne
    @JoinTable(
            name = "performance_measure_deliverable",
            joinColumns = @JoinColumn(name = "performance_measure_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "deliverable_id", referencedColumnName = "id"))
    private PerformanceMeasure performanceMeasure;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id", nullable = true)
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "capability_deliverable",
            joinColumns = @JoinColumn(name = "deliverable_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "capability_id", referencedColumnName = "id"))
    private Capability capability;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(
            name = "completion_deliverable",
            joinColumns = @JoinColumn(name = "deliverable_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "completion_id", referencedColumnName = "id"))
    private Completion completion;

    @ManyToMany
    @JoinTable(
            name = "gantt_target_deliverables",
            joinColumns = @JoinColumn(name = "deliverable_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "target_id", referencedColumnName = "id")
    )
    private Set<Target> targets = new HashSet<>();

    public DeliverableDTO toDto() {
        return new DeliverableDTO(
                id,
                title,
                creationDate,
                status,
                position,
                referenceId,
                children.stream().map(Deliverable::toDto).collect(Collectors.toList()),
                getIdOrNull(parent),
                getIdOrNull(product),
                getIds(targets),
                getIdOrNull(performanceMeasure),
                getIdOrNull(capability),
                getIdOrNull(assignedTo),
                isArchived,
                getDtoOrNull(completion)
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
        Deliverable that = (Deliverable) o;
        return this.hashCode() == that.hashCode();
    }
}
