package mil.af.abms.midas.api.missionthread;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.capability.Capability;
import mil.af.abms.midas.api.missionthread.dto.MissionThreadDTO;

@Entity @Setter @Getter
@Table(name = "mission_thread")
public class MissionThread extends AbstractEntity<MissionThreadDTO> {

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Column(columnDefinition = "TEXT")
    private String title;

    @OneToMany(mappedBy = "missionThread")
    private Set<Capability> capabilities = new HashSet<>();

    public MissionThreadDTO toDto() {
        return new MissionThreadDTO(
                id,
                title,
                creationDate,
                getIds(capabilities),
                isArchived
        );
    }

    @Override
    public int hashCode() { return java.util.Objects.hashCode(title); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissionThread that = (MissionThread) o;
        return this.hashCode() == that.hashCode();
    }
}
