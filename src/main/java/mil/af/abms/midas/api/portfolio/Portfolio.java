package mil.af.abms.midas.api.portfolio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractProductPortfolio;
import mil.af.abms.midas.api.capability.Capability;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;

@Entity @Getter @Setter
@Table(name = "portfolio")
public class Portfolio extends AbstractProductPortfolio<PortfolioDTO> {

    @Deprecated
    @Column(columnDefinition = "TEXT")
    private String ganttNote;

    @Column(columnDefinition = "TEXT")
    protected String description;

    @Deprecated
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime ganttNoteModifiedAt;

    @Deprecated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gantt_note_modified_by")
    private User ganttNoteModifiedBy;

    @Column(columnDefinition = "DATE DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate sprintStartDate = LocalDate.now();

    @Column(columnDefinition = "INT")
    private Integer sprintDurationInDays = 7;

    @OneToMany
    @JoinTable(
            name = "product_portfolio",
            joinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    private Set<Product> products = new HashSet<>();

    @OneToMany
    @JoinTable(
            name = "portfolio_capability",
            joinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "capability_id", referencedColumnName = "id"))
    private Set<Capability> capabilities = new HashSet<>();

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
                getIdsSortedBy(products, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName())),
                gitlabGroupId,
                getIdOrNull(sourceControl),
                personnelDTO,
                vision,
                mission,
                problemStatement,
                capabilities.stream().map(Capability::toDto).collect(Collectors.toList()),
                sprintStartDate,
                sprintDurationInDays
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
