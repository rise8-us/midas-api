package mil.af.abms.midas.api.release;

import javax.transaction.Transactional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.deliverable.DeliverableService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.release.dto.CreateReleaseDTO;
import mil.af.abms.midas.api.release.dto.ReleaseDTO;
import mil.af.abms.midas.api.release.dto.UpdateReleaseDTO;

@Service
public class ReleaseService extends AbstractCRUDService<Release, ReleaseDTO, ReleaseRepository> {

    private DeliverableService deliverableService;

    public ReleaseService(ReleaseRepository repository) {
        super(repository, Release.class, ReleaseDTO.class);
    }

    @Autowired
    public void setDeliverableService(DeliverableService deliverableService) {
        this.deliverableService = deliverableService;
    }

    @Transactional
    public Release create(CreateReleaseDTO dto) {
        Release newRelease = Builder.build(Release.class)
                .with(r -> r.setTitle(dto.getTitle()))
                .with(r -> r.setTargetDate(TimeConversion.getTime(dto.getTargetDate())))
                .get();

        return repository.save(newRelease);
    }

    @Transactional
    public Release updateById(Long id, UpdateReleaseDTO dto) {
        var foundRelease = findById(id);

        foundRelease.setTitle(dto.getTitle());
        foundRelease.setTargetDate(TimeConversion.getTime(dto.getTargetDate()));
        foundRelease.setStatus(dto.getStatus());

        foundRelease.setDeliverables(dto.getDeliverableIds().stream()
                .map(deliverableService::findById).collect(Collectors.toSet()));

        return repository.save(foundRelease);
    }

    public Release updateIsArchived(Long id, IsArchivedDTO dto) {
        Release release = findById(id);

        release.setIsArchived(dto.getIsArchived());

        return repository.save(release);
    }

}
