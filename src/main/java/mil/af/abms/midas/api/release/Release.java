package mil.af.abms.midas.api.release;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.release.dto.ReleaseDTO;

@Entity @Getter @Setter
@Table(name = "releases")
public class Release extends AbstractEntity<ReleaseDTO> {

    @Column(columnDefinition = "VARCHAR(120)", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "VARCHAR(120)")
    private String tagName;

    @Column(columnDefinition = "VARCHAR(255)")
    private String uid;

    @Column(columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime releasedAt;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public ReleaseDTO toDto() {
        return new ReleaseDTO(
            id,
            name,
            description,
            tagName,
            releasedAt,
            getIdOrNull(project)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name + creationDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Release that = (Release) o;
        return this.hashCode() == that.hashCode();
    }
}
