package mil.af.abms.midas.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.announcement.Announcement;
import mil.af.abms.midas.api.announcement.AnnouncementRepository;
import mil.af.abms.midas.api.announcement.AnnouncementService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.UserRepository;

@ExtendWith(SpringExtension.class)
@Import({AnnouncementService.class, SpringContext.class})
public class SpringContextTests {

    @MockBean
    private AnnouncementRepository announcementRepository;
    @MockBean
    private UserRepository userRepository;

    private static AnnouncementService announcementService() {
        return SpringContext.getBean(AnnouncementService.class);
    }
    
    private final Announcement expectedAnnouncement = Builder.build(Announcement.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setMessage("hello world")).get();
    
    @Test
    public void should_return_bean() throws Exception {
        when(announcementRepository.findById(any())).thenReturn(Optional.of(expectedAnnouncement));

        assertThat(announcementService().getClass().getSimpleName()).isEqualTo("AnnouncementService");
        assertThat(announcementService().findById(1L).getMessage()).isEqualTo(expectedAnnouncement.getMessage());
    }
}
