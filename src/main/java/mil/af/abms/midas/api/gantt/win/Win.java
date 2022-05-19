package mil.af.abms.midas.api.gantt.win;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.gantt.AbstractGanttEntity;
import mil.af.abms.midas.api.gantt.win.dto.WinDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;

@Entity @Getter @Setter
@Table(name = "gantt_win")
public class Win extends AbstractGanttEntity<WinDTO> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "gantt_portfolio_win",
            joinColumns = @JoinColumn(name = "win_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"))
    private Portfolio portfolio;

    public WinDTO toDto() {
        return new WinDTO(
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
        Win that = (Win) o;
        return this.hashCode() == that.hashCode();
    }
}
