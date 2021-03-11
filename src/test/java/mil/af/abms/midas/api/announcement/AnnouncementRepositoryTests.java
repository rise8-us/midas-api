package mil.af.abms.midas.api.announcement;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.helper.Builder;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class AnnouncementRepositoryTests {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    AnnouncementRepository announcementRepository;

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    public void should_find_unseen_announcements_for_user() {
        Announcement announcement1 = Builder.build(Announcement.class)
                .with(a -> a.setMessage("foo"))
                .with(a -> a.setCreationDate(NOW)).get();
        Announcement announcement2 = Builder.build(Announcement.class)
                .with(a -> a.setMessage("foo"))
                .with(a -> a.setCreationDate(NOW.minusDays(304L))).get();

        entityManager.persist(announcement1);
        entityManager.persist(announcement2);
        entityManager.flush();

        List<Announcement> a = announcementRepository.findAnnouncementsNewerThan(NOW.minusDays(10L));
        assertThat(a.size()).isEqualTo(1L);
    }
}
