package mil.af.abms.midas.api.performancemeasure;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.capability.CapabilityService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.performancemeasure.dto.CreatePerformanceMeasureDTO;
import mil.af.abms.midas.api.performancemeasure.dto.PerformanceMeasureDTO;
import mil.af.abms.midas.api.performancemeasure.dto.UpdatePerformanceMeasureDTO;

@Service
public class PerformanceMeasureService extends AbstractCRUDService<PerformanceMeasure, PerformanceMeasureDTO, PerformanceMeasureRepository> {

    private CapabilityService capabilityService;

    public PerformanceMeasureService(PerformanceMeasureRepository repository) {
        super(repository, PerformanceMeasure.class, PerformanceMeasureDTO.class);
    }

    @Autowired
    public void setCapabilityService(CapabilityService capabilityService) {
        this.capabilityService = capabilityService;
    }

    @Transactional
    public PerformanceMeasure create(CreatePerformanceMeasureDTO dto) {
        PerformanceMeasure newPerformanceMeasure = Builder.build(PerformanceMeasure.class)
                .with(d -> d.setTitle(dto.getTitle()))
                .with(d -> d.setReferenceId(dto.getReferenceId()))
                .with(d -> d.setCapability(capabilityService.findById(dto.getCapabilityId())))
                .get();

        return repository.save(newPerformanceMeasure);
    }

    @Transactional
    public PerformanceMeasure updateById(Long id, UpdatePerformanceMeasureDTO dto) {
        PerformanceMeasure performanceMeasure = findById(id);

        performanceMeasure.setTitle(dto.getTitle());
        performanceMeasure.setReferenceId(dto.getReferenceId());

        return repository.save(performanceMeasure);
    }

    @Transactional
    public List<PerformanceMeasure> bulkUpdate(List<UpdatePerformanceMeasureDTO> dtos) {
        return dtos.stream().map(d -> updateById(d.getId(), d)).collect(Collectors.toList());
    }

    public PerformanceMeasure updateIsArchived(Long id, IsArchivedDTO dto) {
        PerformanceMeasure performanceMeasure = findById(id);

        performanceMeasure.setIsArchived(dto.getIsArchived());

        return repository.save(performanceMeasure);
    }
}
