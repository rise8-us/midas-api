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
import mil.af.abms.midas.api.problem.dto.ProblemDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;

@Entity @Getter @Setter
@Table(name = "problem")
public class Problem extends AbstractEntity<ProblemDTO> {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

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

    public ProblemDTO toDto() {
        return new ProblemDTO(id, getIdOrNull(createdBy), getIdOrNull(product), text, isCurrent, creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Problem that = (Problem) o;
        return this.hashCode() == that.hashCode();
    }

}
