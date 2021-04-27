package mil.af.abms.midas.api.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.project.dto.ProjectDTO;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.team.Team;

@Entity @Getter @Setter
@Table(name = "project")
public class Project extends AbstractEntity<ProjectDTO> {

    @Column(columnDefinition = "VARCHAR(70)", nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Column(columnDefinition = "BIGINT DEFAULT 0", nullable = false)
    private Long projectJourneyMap = 0L;

    @Column(columnDefinition = "BIGINT", nullable = false)
    private Long gitlabProjectId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToMany
    @JoinTable(
            name = "project_tag",
            joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Tag> tags = new HashSet<>();

    public ProjectDTO toDto() {
        Long teamId = team != null ? team.getId() : null;
        Long portfolioId = product != null ? product.getId() : null;

        return new ProjectDTO(id, name, description, isArchived, creationDate, gitlabProjectId, getTagIds(), teamId,
                projectJourneyMap, portfolioId);
    }

    private Set<Long> getTagIds() { return tags.stream().map(Tag::getId).collect(Collectors.toSet()); }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project that = (Project) o;
        return this.hashCode() == that.hashCode();
    }
}
