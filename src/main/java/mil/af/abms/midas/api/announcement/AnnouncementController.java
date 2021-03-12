package mil.af.abms.midas.api.announcement;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.announcement.dto.AnnouncementDTO;
import mil.af.abms.midas.api.announcement.dto.CreateAnnouncementDTO;
import mil.af.abms.midas.api.announcement.dto.UpdateAnnouncementDTO;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController extends AbstractCRUDController<
        Announcement, AnnouncementDTO, AnnouncementService> {

    @Autowired
    public AnnouncementController(AnnouncementService service) { super(service); }

    @PostMapping
    public AnnouncementDTO create(@Valid @RequestBody CreateAnnouncementDTO createAnnouncementDTO) {
        return service.create(createAnnouncementDTO).toDto();
    }

    @PutMapping("/{id}")
    public AnnouncementDTO update(
            @Valid @RequestBody UpdateAnnouncementDTO updateAnnouncementDTO,
            @PathVariable Long id
    ) {
        return service.update(updateAnnouncementDTO, id).toDto();
    }

}
