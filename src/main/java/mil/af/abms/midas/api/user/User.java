package mil.af.abms.midas.api.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.dto.UserDTO;

@Entity @Getter @Setter
@Table(name = "users")
public class User extends AbstractEntity<UserDTO> {

    @NaturalId(mutable = false)
    @Column(unique = true)
    private String keycloakUid;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String username;

    @Column(columnDefinition = "VARCHAR(100)")
    private String email;

    @Column(columnDefinition = "VARCHAR(100)")
    private String displayName;

    @Column(columnDefinition = "BIGINT")
    private Long dodId;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isDisabled = false;

    @Column(columnDefinition = "BIGINT DEFAULT 0", nullable = false)
    private Long roles = 0L;

    @Column(name = "last_login")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime lastLogin;

    @ManyToMany
    @JoinTable(
            name = "user_team",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Team> teams = new HashSet<>();

    public UserDTO toDto() {
        Set<Long> teamIds = teams.stream().map(Team::getId).collect(Collectors.toSet());
        return new UserDTO(id, keycloakUid, username, email, displayName,
            creationDate, dodId, isDisabled, roles, lastLogin, teamIds);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(keycloakUid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return this.hashCode() == that.hashCode();
    }
}
