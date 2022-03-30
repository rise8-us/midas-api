package mil.af.abms.midas.api.portfolio;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractProductPortfolio;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.product.Product;

@Entity @Getter @Setter
@Table(name = "portfolio")
public class Portfolio extends AbstractProductPortfolio<PortfolioDTO> {

    @OneToMany
    @JoinTable(
            name = "product_portfolio",
            joinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    private Set<Product> products = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(
            name = "portfolio_personnel",
            joinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "personnel_id", referencedColumnName = "id"))
    private Personnel personnel;

    public PortfolioDTO toDto() {
        PersonnelDTO personnelDTO = personnel != null ? personnel.toDto() : new PersonnelDTO();
        return new PortfolioDTO(
                id,
                name,
                description,
                isArchived,
                creationDate,
                getIds(products),
                gitlabGroupId,
                getIdOrNull(sourceControl),
                personnelDTO,
                vision,
                mission,
                problemStatement
        );
    }

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
