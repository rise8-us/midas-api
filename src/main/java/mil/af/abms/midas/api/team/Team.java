package mil.af.abms.midas.api.team;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.user.User;

@Entity @Getter @Setter
@Table(name = "team")
public class Team extends AbstractEntity<TeamDTO> {

    @NaturalId(mutable = true)
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(70)")
    private String name;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    /**
     * @deprecated will be removed since team we are not associating a team with a gitlab group
     */
    @Deprecated
    @Column(columnDefinition = "BIGINT")
    private Long gitlabGroupId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    private User productManager;

    @ManyToOne
    private User designer;

    @ManyToOne
    private User techLead;

    @ManyToMany
    @JoinTable(
            name = "user_team",
            joinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    )
    private Set<User> members = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "personnel_team",
            joinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "personnel_id", referencedColumnName = "id", nullable = false)
    )
    private Set<Personnel> personnels = new HashSet<>();

    public TeamDTO toDto() {
        return new TeamDTO(
                id,
                creationDate,
                name,
                description,
                isArchived,
                getIds(personnels),
                getIdOrNull(productManager),
                getIdOrNull(designer),
                getIdOrNull(techLead),
                getIds(members)
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
        Team that = (Team) o;
        return this.hashCode() == that.hashCode();
    }
}
