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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.TeamEntity;
import mil.af.abms.midas.api.user.dto.UserDTO;

@Entity @Getter @Setter
@Table(name = "users")
public class UserEntity extends AbstractEntity<UserDTO> {

    @NaturalId(mutable = false)
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

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
            name = "user_team",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = true)
    )
    private Set<TeamEntity> team = new HashSet<>();

    public static UserEntity fromDTO(UserDTO userDTO) {
        return Builder.build(UserEntity.class)
                .with(u -> u.setId(userDTO.getId()))
                .with(u -> u.setKeycloakUid(userDTO.getKeycloakUid()))
                .with(u -> u.setUsername(userDTO.getUsername()))
                .with(u -> u.setEmail(userDTO.getEmail()))
                .with(u -> u.setDisplayName(userDTO.getDisplayName()))
                .with(u -> u.setCreationDate(userDTO.getCreationDate()))
                .with(u -> u.setDodId(userDTO.getDodId()))
                .with(u -> u.setRoles(userDTO.getRoles()))
                .with(u -> u.setIsDisabled(userDTO.getIsDisabled())).get();

    }

    public UserDTO toDto() {
        return new UserDTO(id, keycloakUid, username, email, displayName,
                creationDate, dodId, isDisabled, roles);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(keycloakUid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return this.hashCode() == that.hashCode();
    }
}
