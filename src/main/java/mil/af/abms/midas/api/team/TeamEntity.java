package mil.af.abms.midas.api.team;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.product.ProductEntity;
import mil.af.abms.midas.api.team.dto.TeamDTO;
import mil.af.abms.midas.api.user.UserEntity;

@Entity @Getter @Setter
@Table(name = "teams")
public class TeamEntity extends AbstractEntity<TeamDTO> {

    @NaturalId(mutable = true)
    @Column(unique = true)
    private String name;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<ProductEntity> products = new HashSet<>();

    @Column(columnDefinition = "BIGINT", nullable = false)
    private Long gitlabGroupId;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_team",
            joinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false))
    private Set<UserEntity> users = new HashSet<>();

    public TeamDTO toDto() {
        return new TeamDTO(id, name, isArchived, creationDate, gitlabGroupId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamEntity that = (TeamEntity) o;
        return this.hashCode() == that.hashCode();
    }
}
