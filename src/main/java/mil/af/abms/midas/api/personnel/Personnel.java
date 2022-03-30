package mil.af.abms.midas.api.personnel;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.User;

@Entity @Getter @Setter
@Table(name = "personnel")
public class Personnel extends AbstractEntity<PersonnelDTO> {

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne(mappedBy =  "personnel", orphanRemoval = true)
    private Portfolio portfolios;

    @OneToOne(mappedBy = "personnel", orphanRemoval = true)
    private Product product;

    @ManyToMany
    @JoinTable(
            name = "personnel_team",
            joinColumns = @JoinColumn(name = "personnel_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = false)
    )
    private Set<Team> teams = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "personnel_user_admin",
            joinColumns = @JoinColumn(name = "personnel_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    )
    private Set<User> admins = new HashSet<>();

    public PersonnelDTO toDto() {
        return new PersonnelDTO(
                id,
                getIdOrNull(owner),
                getIds(teams),
                getIds(admins)
        );
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Personnel that = (Personnel) o;
        return this.hashCode() == that.hashCode();
    }
}
