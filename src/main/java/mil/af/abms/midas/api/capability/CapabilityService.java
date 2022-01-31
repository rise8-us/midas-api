package mil.af.abms.midas.api.capability;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.function.UnaryOperator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.capability.dto.CapabilityDTO;
import mil.af.abms.midas.api.capability.dto.CreateCapabilityDTO;
import mil.af.abms.midas.api.capability.dto.UpdateCapabilityDTO;
import mil.af.abms.midas.api.deliverable.DeliverableService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;

@Service
public class CapabilityService extends AbstractCRUDService<Capability, CapabilityDTO, CapabilityRepository> {

    private DeliverableService deliverableService;

    private static final UnaryOperator<String> UPDATE_TOPIC = topic -> "/topic/update_" + topic.toLowerCase();

    private final SimpMessageSendingOperations websocket;

    public CapabilityService(CapabilityRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Capability.class, CapabilityDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setDeliverableService(DeliverableService deliverableService) {
        this.deliverableService = deliverableService;
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

    @Override
    public void deleteById(Long id) {
        Capability capabilityToDelete = findById(id);
        removeRelatedDeliverables(capabilityToDelete);
        repository.deleteById(id);
    }

    private void removeRelatedDeliverables(Capability capability) {
       Optional.ofNullable(capability).map(c -> {
           c.getDeliverables().forEach(deliverableService::deleteAllRelatedDeliverables);
           return c;
       }).ifPresent(d -> websocket.convertAndSend(UPDATE_TOPIC.apply(d.getLowercaseClassName()), d.toDto()));;

    }
}
