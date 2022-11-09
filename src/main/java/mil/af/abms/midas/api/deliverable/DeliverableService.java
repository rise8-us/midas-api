package mil.af.abms.midas.api.deliverable;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.DeliverableInterface;
import mil.af.abms.midas.api.capability.CapabilityService;
import mil.af.abms.midas.api.completion.Completion;
import mil.af.abms.midas.api.completion.CompletionService;
import mil.af.abms.midas.api.completion.dto.CreateCompletionDTO;
import mil.af.abms.midas.api.deliverable.dto.CreateDeliverableDTO;
import mil.af.abms.midas.api.deliverable.dto.DeliverableDTO;
import mil.af.abms.midas.api.deliverable.dto.UpdateDeliverableDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.performancemeasure.PerformanceMeasureService;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;

@Service
public class DeliverableService extends AbstractCRUDService<Deliverable, DeliverableDTO, DeliverableRepository> {

    private UserService userService;
    private ProductService productService;
    private PerformanceMeasureService performanceMeasureService;
    private CapabilityService capabilityService;
    private CompletionService completionService;
    private final SimpMessageSendingOperations websocket;

    private static final UnaryOperator<String> TOPIC = clazzName -> "/topic/update_" + clazzName;

    public DeliverableService(DeliverableRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Deliverable.class, DeliverableDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setPerformanceMeasureService(PerformanceMeasureService performanceMeasureService) {
        this.performanceMeasureService = performanceMeasureService;
    }

    @Autowired
    public void setCapabilityService(CapabilityService capabilityService) {
        this.capabilityService = capabilityService;
    }

    @Autowired
    public void setCompletionService(CompletionService completionService) {
        this.completionService = completionService;
    }

    @Transactional
    public Deliverable create(CreateDeliverableDTO dto) {
        var atCandidate =  userService.findByIdOrNull(dto.getAssignedToId());
        User assignedTo = atCandidate != null ? atCandidate : userService.getUserBySecContext();

        CreateCompletionDTO createCompletionDTO = Optional.ofNullable(dto.getCompletion()).isPresent() ?
                dto.getCompletion() : new CreateCompletionDTO();
        Completion completion = completionService.create(createCompletionDTO);

        Deliverable newDeliverable = Builder.build(Deliverable.class)
                .with(d -> d.setTitle(dto.getTitle()))
                .with(d -> d.setReferenceId(dto.getReferenceId()))
                .with(d -> d.setCompletion(completion))
                .with(d -> d.setPosition(dto.getIndex()))
                .with(d -> d.setProduct(productService.findByIdOrNull(dto.getProductId())))
                .with(d -> d.setPerformanceMeasure(performanceMeasureService.findByIdOrNull(dto.getPerformanceMeasureId())))
                .with(d -> d.setParent(findByIdOrNull(dto.getParentId())))
                .with(d -> d.setAssignedTo(assignedTo))
                .with(d -> d.setCapability(capabilityService.findByIdOrNull(dto.getCapabilityId())))
                .get();

        newDeliverable = repository.save(newDeliverable);
        Long parentId = newDeliverable.getId();

        if (dto.getChildren() != null) {
            newDeliverable.setChildren(dto.getChildren().stream().map(d -> {
                d.setParentId(parentId);
                return this.create(d);
            }).collect(Collectors.toSet()));
        }

        return newDeliverable;
    }

    @Transactional
    public Deliverable updateById(Long id, UpdateDeliverableDTO dto) {
        Deliverable deliverable = findById(id);

        completionService.updateById(deliverable.getCompletion().getId(), dto.getCompletion());

        deliverable.setStatus(dto.getStatus());
        deliverable.setTitle(dto.getTitle());
        deliverable.setReferenceId(dto.getReferenceId());
        deliverable.setPosition(dto.getIndex());
        deliverable.setAssignedTo(userService.findByIdOrNull(dto.getAssignedToId()));

        return repository.save(deliverable);
    }

    @Transactional
    public List<Deliverable> bulkUpdate(List<UpdateDeliverableDTO> dtos) {
        return dtos.stream().map(d -> updateById(d.getId(), d)).collect(Collectors.toList());
    }

    public Deliverable updateIsArchived(Long id, IsArchivedDTO dto) {
        Deliverable deliverable = findById(id);

        deliverable.setIsArchived(dto.getIsArchived());

        return repository.save(deliverable);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Deliverable deliverable = findById(id);
        deliverable.getChildren().forEach(d -> repository.deleteById(d.getId()));
        repository.deleteById(id);
        websocket.convertAndSend(TOPIC.apply(deliverable.getLowercaseClassName()), deliverable.toDto());
    }

    @Transactional
    public void deleteAllRelatedDeliverables(Deliverable deliverable) {
        deliverable.getChildren().forEach(this::deleteAllRelatedDeliverables);
        removeRelationIfExists(deliverable.getCapability(), deliverable);
        removeRelationIfExists(deliverable.getPerformanceMeasure(), deliverable);
        repository.deleteById(deliverable.getId());
    }

    protected void removeRelationIfExists(DeliverableInterface deliverable, Deliverable deliverableToDelete) {
        Optional.ofNullable(deliverable).map(d -> {
            var deliverables = d.getDeliverables().stream().filter(m -> !m.equals(deliverableToDelete)).collect(Collectors.toSet());
            d.setDeliverables(deliverables);
            return d;
        }).ifPresent(d -> websocket.convertAndSend(TOPIC.apply(d.getLowercaseClassName()), d.toDto()));
    }
}
