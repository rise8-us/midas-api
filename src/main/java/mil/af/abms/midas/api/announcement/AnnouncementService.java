package mil.af.abms.midas.api.announcement;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.announcement.dto.AnnouncementDTO;
import mil.af.abms.midas.api.announcement.dto.CreateAnnouncementDTO;
import mil.af.abms.midas.api.announcement.dto.UpdateAnnouncementDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.UserEntity;
import mil.af.abms.midas.api.user.UserRepository;

@Service
public class AnnouncementService extends AbstractCRUDService<AnnouncementEntity, AnnouncementDTO, AnnouncementRepository> {

    UserRepository userRepository;

    @Autowired
    public AnnouncementService(AnnouncementRepository repository, UserRepository userRepository) {
        super(repository, AnnouncementEntity.class, AnnouncementDTO.class);
        this.userRepository = userRepository;
    }

    @Transactional
    public AnnouncementEntity create(CreateAnnouncementDTO dto) {
        AnnouncementEntity announcement = Builder.build(AnnouncementEntity.class)
                .with(a -> a.setMessage(dto.getMessage())).get();
        return repository.save(announcement);
    }

    public AnnouncementEntity update(UpdateAnnouncementDTO dto, Long id) {
        AnnouncementEntity announcement = findById(id);
        announcement.setMessage(dto.getMessage());
        announcement.setCreationDate(LocalDateTime.now());
        return repository.save(announcement);
    }

    public List<AnnouncementEntity> getUnseenAnnouncements(UserEntity user) {
        LocalDateTime announcementsNewThan = getAnnouncementsNoOlderThanMaxDays(user.getLastLogin(), 30L);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return repository.findAnnouncementsNewerThan(announcementsNewThan);
    }

    private LocalDateTime getAnnouncementsNoOlderThanMaxDays(LocalDateTime date, Long maxDaysPast) {
        LocalDateTime now = LocalDateTime.now();
        return date != null && date.isAfter(now.minusDays(maxDaysPast)) ? date : now.minusDays(maxDaysPast);
    }
}
