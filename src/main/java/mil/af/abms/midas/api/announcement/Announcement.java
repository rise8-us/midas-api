package mil.af.abms.midas.api.announcement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.announcement.dto.AnnouncementDTO;

@Entity @Getter @Setter
@Table(name = "announcement")
public class Announcement extends AbstractEntity<AnnouncementDTO> {

    @Column(columnDefinition = "TEXT")
    private String message;

    @Override
    public AnnouncementDTO toDto() {
        return new AnnouncementDTO(id, creationDate, message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Announcement that = (Announcement) o;
        return this.hashCode() == that.hashCode();
    }
}
