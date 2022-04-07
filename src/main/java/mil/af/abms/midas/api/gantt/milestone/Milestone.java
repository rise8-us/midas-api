package mil.af.abms.midas.api.gantt.milestone;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.gantt.AbstractGanttEntity;
import mil.af.abms.midas.api.gantt.milestone.dto.MilestoneDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;

@Entity @Getter @Setter
@Table(name = "gantt_milestone")
public class Milestone extends AbstractGanttEntity<MilestoneDTO> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "gantt_portfolio_milestone",
            joinColumns = @JoinColumn(name = "milestone_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"))
    private Portfolio portfolio;

    public MilestoneDTO toDto() {
        return new MilestoneDTO(
                id,
                dueDate,
                title,
                description,
                getIdOrNull(portfolio)
        );
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Milestone that = (Milestone) o;
        return this.hashCode() == that.hashCode();
    }
}
