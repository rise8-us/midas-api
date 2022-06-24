package mil.af.abms.midas.api.announcement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.announcement.dto.CreateAnnouncementDTO;
import mil.af.abms.midas.api.announcement.dto.UpdateAnnouncementDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserRepository;

@ExtendWith(SpringExtension.class)
@Import(AnnouncementService.class)
class AnnouncementServiceTests {

    @Autowired
    private AnnouncementService announcementService;
    @MockBean
    private AnnouncementRepository announcementRepository;
    @MockBean
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Announcement> announcementCaptor;
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Captor
    ArgumentCaptor<LocalDateTime> dateCaptor;

    private static final LocalDateTime LAST_TIME_LOGGED_IN = LocalDateTime.now().minusDays(10L);
    private static final LocalDateTime NOW = LocalDateTime.now();

    Announcement oldAnnouncement = Builder.build(Announcement.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setMessage("Test Test Test, is this thing on?"))
            .with(a -> a.setCreationDate(LocalDateTime.now().minusDays(11L))).get();
    Announcement newAnnouncement = Builder.build(Announcement.class)
            .with(a -> a.setId(2L))
            .with(a -> a.setMessage("hot mic! hot mic!"))
            .with(a -> a.setCreationDate(LocalDateTime.now().minusDays(11L))).get();

    @Test
    void should_create_announcement() {
        CreateAnnouncementDTO createAnnouncementDTO = Builder.build(CreateAnnouncementDTO.class)
                .with(d -> d.setMessage("This is my announcement")).get();

        when(announcementRepository.save(any())).thenReturn(new Announcement());
        announcementService.create(createAnnouncementDTO);
        verify(announcementRepository).save(announcementCaptor.capture());

        Announcement announcementCaptured = announcementCaptor.getValue();
        assertThat(announcementCaptured.getMessage()).isEqualTo(createAnnouncementDTO.getMessage());
    }

    @Test
    void should_update_announcement() {
        UpdateAnnouncementDTO updateAnnouncementDTO = Builder.build(UpdateAnnouncementDTO.class)
                .with(d -> d.setMessage("updated announcement")).get();

        when(announcementRepository.findById(any())).thenReturn(Optional.of(new Announcement()));
        when(announcementRepository.save(any())).thenReturn(new Announcement());
        announcementService.update(updateAnnouncementDTO, 1L);
        verify(announcementRepository).save(announcementCaptor.capture());

        Announcement announcementCaptured = announcementCaptor.getValue();
        assertThat(announcementCaptured.getMessage()).isEqualTo(updateAnnouncementDTO.getMessage());
    }

    @Test
    void should_return_unseen_announcements() {
        User user = Builder.build(User.class).with(u -> u.setId(1L)).get();
        when(userRepository.save(any())).thenReturn(new User());
        when(announcementRepository.findAnnouncementsNewerThan(any())).thenReturn(List.of(newAnnouncement));

        announcementService.getUnseenAnnouncements(user);

        verify(userRepository).save(userCaptor.capture());
        verify(announcementRepository).findAnnouncementsNewerThan(dateCaptor.capture());
        LocalDateTime lastLogin = userCaptor.getValue().getLastLogin();
        LocalDateTime dateCaptured = dateCaptor.getValue();

        assertThat(dateCaptured).isEqualToIgnoringSeconds(NOW.minusDays(30L));
        assertThat(lastLogin).isEqualToIgnoringSeconds(NOW);
    }

    @Test
    void should_return_announcements_within_30_days() throws Exception {
        Class[] clazz = new Class[2];
        clazz[0] = LocalDateTime.class;
        clazz[1] = Long.class;

        Method getAnnouncement = announcementService.getClass()
                .getDeclaredMethod("getAnnouncementsNoOlderThanMaxDays", clazz
        );
        getAnnouncement.setAccessible(true);
        LocalDateTime lastLoginMoreRecentThanMaxDays = (LocalDateTime) getAnnouncement.invoke(announcementService, NOW.minusDays(5L), 25L);
        LocalDateTime lastLoginNull = (LocalDateTime) getAnnouncement.invoke(announcementService, null, 25L);
        LocalDateTime lastLoginOlderThanMaxDays = (LocalDateTime) getAnnouncement.invoke(announcementService, NOW.minusDays(31L), 25L);

        assertThat(lastLoginMoreRecentThanMaxDays).isEqualToIgnoringSeconds(NOW.minusDays(5L));
        assertThat(lastLoginNull).isEqualToIgnoringSeconds(NOW.minusDays(25L));
        assertThat(lastLoginOlderThanMaxDays).isEqualToIgnoringSeconds(NOW.minusDays(25L));

    }
}
