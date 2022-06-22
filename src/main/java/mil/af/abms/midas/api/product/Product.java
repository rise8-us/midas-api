package mil.af.abms.midas.api.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractProductPortfolio;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.release.Release;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.dto.TagDTO;
import mil.af.abms.midas.enums.RoadmapType;

@Entity @Getter @Setter
@Table(name = "product")
public class Product extends AbstractProductPortfolio<ProductDTO> {

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'MANUAL'", nullable = false)
    private RoadmapType roadmapType = RoadmapType.MANUAL;

    @OneToMany(mappedBy = "product")
    private Set<Project> projects = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "product_tag",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToOne
    @JoinTable(
            name = "product_personnel",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "personnel_id", referencedColumnName = "id")
    )
    private Personnel personnel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_portfolio",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "portfolio_id", referencedColumnName = "id"))
    private Portfolio portfolio;

    public ProductDTO toDto() {
        Set<TagDTO> tagDTOs = tags.stream().map(Tag::toDto).collect(Collectors.toSet());
        PersonnelDTO personnelDTO = personnel != null ? personnel.toDto() : new PersonnelDTO();
        return new ProductDTO(
                id,
                name,
                description,
                personnelDTO,
                isArchived,
                creationDate,
                getIds(projects),
                tagDTOs,
                gitlabGroupId,
                getIdOrNull(sourceControl),
                vision,
                mission,
                problemStatement,
                roadmapType,
                getIdOrNull(portfolio),
                getDtoOrNull(getLatestRelease())
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
        Product that = (Product) o;
        return this.hashCode() == that.hashCode();
    }

    public Release getLatestRelease() {
        Set<Release> latestReleases = this.getProjects().stream().map(Project::getLatestRelease).collect(Collectors.toSet());
        return latestReleases.stream().max(Comparator.comparing(Release::getReleasedAt, Comparator.nullsFirst(Comparator.naturalOrder()))).orElse(null);
    }

}
