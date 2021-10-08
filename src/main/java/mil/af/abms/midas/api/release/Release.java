package mil.af.abms.midas.api.release;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.deliverable.Deliverable;
import mil.af.abms.midas.api.release.dto.ReleaseDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@Entity @Getter @Setter
@Table(name = "releases")
public class Release extends AbstractEntity<ReleaseDTO> {

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime targetDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'NOT_STARTED'", nullable = false)
    private ProgressionStatus status = ProgressionStatus.NOT_STARTED;

    @ManyToMany
    @JoinTable(
            name = "release_deliverable",
            joinColumns = @JoinColumn(name = "release_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "deliverable_id", referencedColumnName = "id", nullable = false)
    )
    private Set<Deliverable> deliverables = new HashSet<>();

    public ReleaseDTO toDto() {
        return new ReleaseDTO(
            id,
            title,
            creationDate,
            targetDate,
            status,
            getIds(deliverables),
                isArchived
        );
    }

}
