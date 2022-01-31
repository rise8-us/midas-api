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
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.capability.Capability;
import mil.af.abms.midas.api.deliverable.dto.DeliverableDTO;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.performancemeasure.PerformanceMeasure;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.release.Release;
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

    @ManyToMany
    @JoinTable(
            name = "release_deliverable",
            joinColumns = @JoinColumn(name = "release_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "deliverable_id", referencedColumnName = "id", nullable = false)
    )
    private Set<Release> releases = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_measure_id", nullable = true)
    private PerformanceMeasure performanceMeasure;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id", nullable = true)
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capability_id", nullable = true)
    private Capability capability;

    @ManyToOne
    @JoinColumn(name = "epic_id", nullable = true)
    private Epic epic;

    public DeliverableDTO toDto() {
        return new DeliverableDTO(
                id,
                title,
                creationDate,
                status,
                position,
                referenceId,
                getIds(releases),
                children.stream().map(Deliverable::toDto).collect(Collectors.toList()),
                getIdOrNull(parent),
                getIdOrNull(product),
                getIdOrNull(performanceMeasure),
                getIdOrNull(capability),
                getIdOrNull(assignedTo),
                getIdOrNull(epic),
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
        Deliverable that = (Deliverable) o;
        return this.hashCode() == that.hashCode();
    }
}
