package mil.af.abms.midas.api.gantt.target;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.gantt.AbstractGanttEntity;
import mil.af.abms.midas.api.gantt.target.dto.TargetDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;

@Entity @Getter @Setter
@Table(name = "gantt_target")
public class Target extends AbstractGanttEntity<TargetDTO> {

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate startDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "gantt_portfolio_target",
            joinColumns = @JoinColumn(name = "target_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"))
    private Portfolio portfolio;

    public TargetDTO toDto() {
        return new TargetDTO(
                id,
                creationDate,
                startDate,
                dueDate,
                title,
                description,
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
        Target that = (Target) o;
        return this.hashCode() == that.hashCode();
    }
}
