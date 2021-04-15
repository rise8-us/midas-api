package mil.af.abms.midas.api.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.application.dto.ApplicationDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.user.User;

@Entity @Getter @Setter
@Table(name = "applications")
public class Application extends AbstractEntity<ApplicationDTO> {

    @Column(columnDefinition = "VARCHAR(70)", nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @ManyToOne
    @JoinColumn(name = "product_manager_id")
    private User productManager;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @OneToMany(mappedBy = "application")
    private Set<Project> projects = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "applications_tags",
            joinColumns = @JoinColumn(name = "application_id", referencedColumnName = "id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Tag> tags = new HashSet<>();

    public ApplicationDTO toDto() {
        Long productManagerId = productManager != null ? productManager.getId() : null;
        Long portfolioId = portfolio != null ? portfolio.getId() : null;
        return new ApplicationDTO(id, name, productManagerId, description, getProjectIds(), isArchived, creationDate,
                portfolioId, getTagIds());
    }

    private Set<Long> getProjectIds() { return projects.stream().map(Project::getId).collect(Collectors.toSet()); }

    private Set<Long> getTagIds() { return tags.stream().map(Tag::getId).collect(Collectors.toSet()); }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return this.hashCode() == that.hashCode();
    }

}
