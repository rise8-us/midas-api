package mil.af.abms.midas.api.announcement;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;

public class AnnouncementRepositoryTests extends RepositoryTestHarness {

    @Autowired
    AnnouncementRepository announcementRepository;

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    public void should_find_unseen_announcements_for_user() {
        Announcement announcement1 = Builder.build(Announcement.class)
                .with(a -> a.setMessage("foo"))
                .with(a -> a.setCreationDate(NOW))
                .get();
        Announcement announcement2 = Builder.build(Announcement.class)
                .with(a -> a.setMessage("foo"))
                .with(a -> a.setCreationDate(NOW.minusDays(304L)))
                .get();

        entityManager.persist(announcement1);
        entityManager.persist(announcement2);
        entityManager.flush();

        List<Announcement> a = announcementRepository.findAnnouncementsNewerThan(NOW.minusDays(10L));
        assertThat(a.size()).isEqualTo(1L);
    }
}
