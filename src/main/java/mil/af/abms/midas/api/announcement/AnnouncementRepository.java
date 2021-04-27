package mil.af.abms.midas.api.announcement;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.announcement.dto.AnnouncementDTO;

public interface AnnouncementRepository extends RepositoryInterface<Announcement, AnnouncementDTO> {

    @Query(value = "SELECT * FROM announcement a WHERE a.creation_date > :date", nativeQuery = true)
    List<Announcement> findAnnouncementsNewerThan(@Param("date") LocalDateTime date);
}
