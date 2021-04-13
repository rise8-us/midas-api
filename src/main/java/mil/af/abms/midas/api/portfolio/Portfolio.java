package mil.af.abms.midas.api.portfolio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.application.Application;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
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

    @ManyToOne
    @JoinColumn(name = "portfolio_manager_id")
    private User portfolioManager;

    @OneToMany(mappedBy = "portfolio")
    private Set<Application> applications = new HashSet<>();

    public PortfolioDTO toDto() {
        Long portfolioManagerId = portfolioManager != null ? portfolioManager.getId() : null;
        return new PortfolioDTO(id, name, portfolioManagerId, description, getApplicationIds(), isArchived, creationDate);
    }

    private Set<Long> getApplicationIds() { return applications.stream().map(Application::getId).collect(Collectors.toSet()); }

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
