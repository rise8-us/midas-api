package mil.af.abms.midas.api.roadmap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractTimeConstrainedEntity;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.roadmap.dto.RoadmapDTO;
import mil.af.abms.midas.enums.RoadmapStatus;

@Entity @Getter @Setter
@Table(name = "roadmap")
public class Roadmap extends AbstractTimeConstrainedEntity<RoadmapDTO> {

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'FUTURE'", nullable = false)
    private RoadmapStatus status = RoadmapStatus.FUTURE;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isHidden;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public RoadmapDTO toDto() {
        return new RoadmapDTO(
                id,
                title,
                status,
                creationDate,
                description,
                isHidden,
                getIdOrNull(product),
                startDate,
                dueDate,
                completedAt
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
        Roadmap that = (Roadmap) o;
        return this.hashCode() == that.hashCode();
    }
}
