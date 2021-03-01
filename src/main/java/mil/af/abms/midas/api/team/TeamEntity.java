package mil.af.abms.midas.api.team;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(columnDefinition = "BIGINT", nullable = false)
    private Long gitlabGroupId;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_team",
            joinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false))
    private Set<UserEntity> users = new HashSet<>();

    public static TeamEntity fromDTO(TeamDTO teamDTO) {
        return Builder.build(TeamEntity.class)
                .with(t -> t.setId(teamDTO.getId()))
                .with(t -> t.setName(teamDTO.getName()))
                .with(t -> t.setIsArchived(teamDTO.getIsArchived()))
                .with(t -> t.setGitlabGroupId(teamDTO.getGitlabGroupId()))
                .with(t -> t.setCreationDate(teamDTO.getCreationDate())).get();
    }

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
