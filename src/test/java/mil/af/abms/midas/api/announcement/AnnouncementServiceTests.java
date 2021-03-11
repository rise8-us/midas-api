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
import mil.af.abms.midas.api.user.UserEntity;
import mil.af.abms.midas.api.user.UserRepository;

@ExtendWith(SpringExtension.class)
@Import(AnnouncementService.class)
public class AnnouncementServiceTests {

    @Autowired
    private AnnouncementService announcementService;
    @MockBean
    private AnnouncementRepository announcementRepository;
    @MockBean
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<AnnouncementEntity> announcementCaptor;
    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;
    @Captor
    ArgumentCaptor<LocalDateTime> dateCaptor;

    private static final LocalDateTime LAST_TIME_LOGGED_IN = LocalDateTime.now().minusDays(10L);
    private static final LocalDateTime NOW = LocalDateTime.now();

    AnnouncementEntity oldAnnouncement = Builder.build(AnnouncementEntity.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setMessage("Test Test Test, is this thing on?"))
            .with(a -> a.setCreationDate(LocalDateTime.now().minusDays(11L))).get();
    AnnouncementEntity newAnnouncement = Builder.build(AnnouncementEntity.class)
            .with(a -> a.setId(2L))
            .with(a -> a.setMessage("hot mic! hot mic!"))
            .with(a -> a.setCreationDate(LocalDateTime.now().minusDays(11L))).get();

    @Test
    public void should_create_announcement() {
        CreateAnnouncementDTO createAnnouncementDTO = Builder.build(CreateAnnouncementDTO.class)
                .with(d -> d.setMessage("This is my announcement")).get();

        when(announcementRepository.save(any())).thenReturn(new AnnouncementEntity());
        announcementService.create(createAnnouncementDTO);
        verify(announcementRepository).save(announcementCaptor.capture());

        AnnouncementEntity announcementCaptured = announcementCaptor.getValue();
        assertThat(announcementCaptured.getMessage()).isEqualTo(createAnnouncementDTO.getMessage());
    }

    @Test
    public void should_update_announcement() {
        UpdateAnnouncementDTO updateAnnouncementDTO = Builder.build(UpdateAnnouncementDTO.class)
                .with(d -> d.setMessage("updated announcement")).get();

        when(announcementRepository.findById(any())).thenReturn(Optional.of(new AnnouncementEntity()));
        when(announcementRepository.save(any())).thenReturn(new AnnouncementEntity());
        announcementService.update(updateAnnouncementDTO, 1L);
        verify(announcementRepository).save(announcementCaptor.capture());

        AnnouncementEntity announcementCaptured = announcementCaptor.getValue();
        assertThat(announcementCaptured.getMessage()).isEqualTo(updateAnnouncementDTO.getMessage());
    }

    @Test
    public void should_return_unseen_announcements() {
        UserEntity user = Builder.build(UserEntity.class).with(u -> u.setId(1L)).get();
        when(userRepository.save(any())).thenReturn(new UserEntity());
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
    public void should_return_announcements_within_30_days() throws Exception {
        Class[] clazz = new Class[2];
        clazz[0] = LocalDateTime.class;
        clazz[1] = Long.class;

        Method getAnnouncement = announcementService.getClass()
                .getDeclaredMethod("getAnnouncementsNoOlderThanMaxDays", clazz
        );
        getAnnouncement.setAccessible(true);
        LocalDateTime lastLogin = (LocalDateTime) getAnnouncement.invoke(announcementService, NOW.minusDays(5L), 25L);
        LocalDateTime lastLoginNull = (LocalDateTime) getAnnouncement.invoke(announcementService, null, 25L);

        assertThat(lastLogin).isEqualToIgnoringSeconds(NOW.minusDays(5L));
        assertThat(lastLoginNull).isEqualToIgnoringSeconds(NOW.minusDays(25L));
    }
}
