package mil.af.abms.midas.api.gantt.target;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.deliverable.Deliverable;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.gantt.AbstractGanttEntity;
import mil.af.abms.midas.api.gantt.target.dto.TargetDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;

@Entity @Getter @Setter
@Table(name = "gantt_target")
public class Target extends AbstractGanttEntity<TargetDTO> {

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "gantt_portfolio_target",
            joinColumns = @JoinColumn(name = "target_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"))
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Target parent;

    @OneToMany(mappedBy = "parent")
    private Set<Target> children = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "gantt_target_gitlab_epic",
            joinColumns = @JoinColumn(name = "target_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "epic_id", referencedColumnName = "id")
    )
    private Set<Epic> epics = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "gantt_target_deliverables",
            joinColumns = @JoinColumn(name = "target_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "deliverable_id", referencedColumnName = "id")
    )
    private Set<Deliverable> deliverables = new HashSet<>();

    public TargetDTO toDto() {
        return new TargetDTO(
                id,
                startDate,
                dueDate,
                title,
                description,
                getIdOrNull(portfolio),
                getIdOrNull(parent),
                children.stream().map(Target::toDto).collect(Collectors.toList()),
                epics.stream().map(Epic::toDto).collect(Collectors.toSet()),
                deliverables.stream().map(Deliverable::toDto).collect(Collectors.toSet())
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
        Target that = (Target) o;
        return this.hashCode() == that.hashCode();
    }
}
