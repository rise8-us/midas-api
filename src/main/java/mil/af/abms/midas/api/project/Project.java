package mil.af.abms.midas.api.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.coverage.Coverage;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.project.dto.ProjectDTO;
import mil.af.abms.midas.api.release.Release;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.SyncStatus;

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

    @Column(columnDefinition = "INT", nullable = false)
    private Integer gitlabProjectId;

    @Column(columnDefinition = "TEXT")
    private String webUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'SYNCED'")
    private SyncStatus releaseSyncStatus = SyncStatus.SYNCED;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'SYNCED'")
    private SyncStatus issueSyncStatus = SyncStatus.SYNCED;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    private SourceControl sourceControl;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "project")
    private Set<Coverage> coverages =  new HashSet<>();

    @OneToMany(mappedBy = "project")
    private Set<Release> releases =  new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "project_tag",
            joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Tag> tags = new HashSet<>();

    public ProjectDTO toDto() {
        return new ProjectDTO(
                id,
                name,
                description,
                isArchived,
                creationDate,
                gitlabProjectId,
                webUrl,
                releaseSyncStatus,
                issueSyncStatus,
                getTagIds(),
                getIdOrNull(team),
                projectJourneyMap,
                getIdOrNull(product),
                getCurrentCoverage().toDto(),
                getIdOrNull(sourceControl),
                getIdOrNull(owner),
                getDtoOrNull(getLatestRelease())
        );
    }

    public Coverage getCurrentCoverage() {
        return coverages.stream().max(Comparator.comparing(Coverage::getId)).orElse(new Coverage());
    }

    public Release getLatestRelease() {
        return releases.stream().max(Comparator.comparing(Release::getReleasedAt)).orElse(new Release());
    }

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

    private Set<Long> getTagIds() { return tags.stream().map(Tag::getId).collect(Collectors.toSet()); }
}
