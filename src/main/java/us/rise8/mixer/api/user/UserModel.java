package us.rise8.mixer.api.user;

import javax.persistence.Entity;
import javax.persistence.Table;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import us.rise8.mixer.api.AbstractEntity;
import us.rise8.mixer.api.helper.Builder;
import us.rise8.mixer.api.user.dto.UserDTO;

@Entity
@Getter @Setter
@Table(name = "users")
public class UserModel extends AbstractEntity<UserDTO> {

    @NaturalId(mutable = false)
    private String keycloakUid;
    private String username;
    private String email;
    private String displayName;
    private Long dodId;
    private Boolean isDisabled = false;
    private Long roles = 0L;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    public static UserModel fromDTO(UserDTO userDTO) {
        return Builder.build(UserModel.class)
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
        UserModel that = (UserModel) o;
        return this.hashCode() == that.hashCode();
    }
}
