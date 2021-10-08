package mil.af.abms.midas.api.capability;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.capability.dto.CapabilityDTO;
import mil.af.abms.midas.api.capability.dto.CreateCapabilityDTO;
import mil.af.abms.midas.api.capability.dto.UpdateCapabilityDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;

@Service
public class CapabilityService extends AbstractCRUDService<Capability, CapabilityDTO, CapabilityRepository> {

    public CapabilityService(CapabilityRepository repository) {
        super(repository, Capability.class, CapabilityDTO.class);
    }

    @Transactional
    public Capability create(CreateCapabilityDTO dto) {
        Capability newCapability = Builder.build(Capability.class)
                .with(c -> c.setTitle(dto.getTitle()))
                .with(c -> c.setDescription(dto.getDescription()))
                .with(c -> c.setReferenceId(dto.getReferenceId()))
                .get();

        return repository.save(newCapability);
    }

    @Transactional
    public Capability updateById(Long id, UpdateCapabilityDTO dto) {
        Capability capability = findById(id);

        capability.setTitle(dto.getTitle());
        capability.setDescription(dto.getDescription());
        capability.setReferenceId(dto.getReferenceId());

        return repository.save(capability);
    }

    public Capability updateIsArchived(Long id, IsArchivedDTO dto) {
        Capability capability = findById(id);

        capability.setIsArchived(dto.getIsArchived());

        return repository.save(capability);
    }

}
