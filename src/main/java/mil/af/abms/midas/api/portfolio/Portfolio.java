package mil.af.abms.midas.api.portfolio;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;

@Entity @Getter @Setter
@Table(name = "portfolios")
public class Portfolio extends AbstractEntity<PortfolioDTO> {

    @Column(columnDefinition = "VARCHAR(70)", nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Column(columnDefinition = "BIGINT")
    private User lead;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();

    public PortfolioDTO toDto() {
        Long leadId = null;

        if (lead != null) {
            leadId = lead.getId();
        }

        return new PortfolioDTO(id, name, leadId, description, getProductIds(), isArchived, creationDate);
    }

    private Set<Long> getProductIds() { return products.stream().map(Product::getId).collect(Collectors.toSet()); }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio that = (Portfolio) o;
        return this.hashCode() == that.hashCode();
    }

}
