package mil.af.abms.midas.api.problem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.problem.dto.ProblemDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;

@Entity @Getter @Setter
@Table(name = "problem")
public class Problem extends AbstractEntity<ProblemDTO> {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String problem;

    @Column(columnDefinition = "BIT(1) DEFAULT 1", nullable = false)
    private Boolean isCurrent = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_problem",
            joinColumns = @JoinColumn(name = "problem_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id")
    )
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "portfolio_problem",
            joinColumns = @JoinColumn(name = "problem_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id")
    )
    private Portfolio portfolio;

    public ProblemDTO toDto() {
        return new ProblemDTO(id, getCreatedById(), getProductId(), getPortfolioId(), problem, isCurrent, creationDate);
    }

    public Long getCreatedById() { return createdBy != null ? createdBy.getId() : null; }
    public Long getProductId() { return product != null ? product.getId() : null; }
    public Long getPortfolioId() { return  portfolio != null ? portfolio.getId() : null; }

    @Override
    public int hashCode() {
        return Objects.hashCode(problem);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Problem that = (Problem) o;
        return this.hashCode() == that.hashCode();
    }

}
