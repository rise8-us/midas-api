package mil.af.abms.midas.api.feature;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.feature.dto.FeatureDTO;
import mil.af.abms.midas.api.product.Product;

@Entity @Getter @Setter
@Table(name = "feature")
public class Feature extends AbstractEntity<FeatureDTO> {

    @Column(nullable = false, columnDefinition = "VARCHAR(70)")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "INT")
    private Integer position;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public FeatureDTO toDto() {
        return new FeatureDTO(id, title, creationDate, description, getIdOrNull(product), position);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature that = (Feature) o;
        return this.hashCode() == that.hashCode();
    }
}
