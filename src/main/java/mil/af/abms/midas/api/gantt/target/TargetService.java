package mil.af.abms.midas.api.gantt.target;

import javax.transaction.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.deliverable.Deliverable;
import mil.af.abms.midas.api.deliverable.DeliverableService;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.gantt.target.dto.CreateTargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.TargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.TargetInterfaceDTO;
import mil.af.abms.midas.api.gantt.target.dto.UpdateTargetDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@Service
public class TargetService extends AbstractCRUDService<Target, TargetDTO, TargetRepository> {

    private PortfolioService portfolioService;
    private final SimpMessageSendingOperations websocket;
    private EpicService epicService;
    private DeliverableService deliverableService;

    public TargetService(TargetRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Target.class, TargetDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setEpicService(EpicService epicService) {
        this.epicService = epicService;
    }

    @Autowired
    public void setDeliverableService(DeliverableService deliverableService) {
        this.deliverableService = deliverableService;
    }

    @Autowired
    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Transactional
    public Target create(CreateTargetDTO dto) {
        Target newTarget = Builder.build(Target.class)
                .with(t -> t.setPortfolio(portfolioService.findById(dto.getPortfolioId())))
                .with(t -> t.setParent(findByIdOrNull(dto.getParentId())))
                .get();

        linkEpics(dto.getEpicIds(), newTarget);
        linkDeliverables(dto.getDeliverableIds(), newTarget);

        updateCommonFields(dto, newTarget);

        var targetCreated = repository.save(newTarget);
        sendParentUpdatedWebsocketMessage(targetCreated, true);

        return targetCreated;
    }

    @Transactional
    public Target updateById(Long id, UpdateTargetDTO dto) {
        Target foundTarget = findById(id);

        linkEpics(dto.getEpicIds(), foundTarget);
        linkDeliverables(dto.getDeliverableIds(), foundTarget);
        updateCommonFields(dto, foundTarget);

        return repository.save(foundTarget);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Target targetToDelete = findById(id);
        removeLinks(targetToDelete);
        sendParentUpdatedWebsocketMessage(targetToDelete, false);
        targetToDelete.getChildren().forEach(t -> deleteById(t.getId()));
        repository.deleteById(id);
    }

    protected void updateCommonFields(TargetInterfaceDTO dto, Target target) {
        target.setStartDate(dto.getStartDate());
        target.setDueDate(dto.getDueDate());
        target.setTitle(dto.getTitle());
        target.setDescription(dto.getDescription());
        Optional.ofNullable(dto.getIsPriority()).ifPresent(value -> {
            target.setIsPriority(dto.getIsPriority());
        });
    }

    protected void linkGitlabEpic(Long epicId, Target target) {
        Epic foundEpic = epicService.findByIdOrNull(epicId);
        Set<Epic> newEpicSet = new HashSet<>(target.getEpics());

        Optional.ofNullable(foundEpic).ifPresentOrElse(epic -> {
            if (Optional.ofNullable(epic.getProduct()).isPresent()) {
                newEpicSet.add(epicService.updateByIdForProduct(epic.getId()));
            }
            if (Optional.ofNullable(epic.getPortfolio()).isPresent()) {
                newEpicSet.add(epicService.updateByIdForPortfolio(epic.getId()));
            }
            target.setEpics(newEpicSet);
        }, () -> target.setEpics(Set.of()));

    }

    protected void linkDeliverable(Long deliverableId, Target target) {
            Deliverable foundDeliverable = deliverableService.findByIdOrNull(deliverableId);

            Set<Deliverable> newDeliverableSet = new HashSet<>();
            newDeliverableSet.add(foundDeliverable);
            newDeliverableSet.addAll(target.getDeliverables());

            target.setDeliverables(newDeliverableSet);
    }

    private void sendParentUpdatedWebsocketMessage(Target target, boolean isAdded) {
        Optional.ofNullable(target.getParent()).ifPresent(parent -> {
            if (isAdded) { parent.getChildren().add(target); }
            else {
                parent.setChildren(parent.getChildren().stream()
                        .filter(a -> !a.getId().equals(target.getId()))
                        .collect(Collectors.toList())
                );
            }
            websocket.convertAndSend("/topic/update_target", parent.toDto());
        });
    }

    private void linkEpics(Set<Long> incomingIds, Target target) {
        Optional.ofNullable(incomingIds).ifPresent(ids -> {
            Set<Long> idsToRemove = target.getEpics().stream().map(Epic::getId).collect(Collectors.toSet());
            Set<Long> idsToAdd = new HashSet<>(ids);
            idsToAdd.removeAll(idsToRemove);
            idsToRemove.removeAll(ids);

            idsToRemove.forEach(id -> {
                target.setEpics(target.getEpics().stream().filter(e -> !Objects.equals(e.getId(), id)).collect(Collectors.toSet()));
            });
            idsToAdd.forEach(id -> {
                linkGitlabEpic(id, target);
            });
        });
    }

    private void linkDeliverables(Set<Long> incomingIds, Target target) {
        Optional.ofNullable(incomingIds).ifPresent(ids -> {
            Set<Long> idsToRemove = target.getDeliverables().stream().map(Deliverable::getId).collect(Collectors.toSet());
            Set<Long> idsToAdd = new HashSet<>(ids);
            idsToAdd.removeAll(idsToRemove);
            idsToRemove.removeAll(ids);

            idsToRemove.forEach(id -> {
                target.setDeliverables(target.getDeliverables().stream().filter(e -> !Objects.equals(e.getId(), id)).collect(Collectors.toSet()));
            });
            idsToAdd.forEach(id -> {
                linkDeliverable(id, target);
            });
        });
    }

    private void removeLinks(Target target) {
        target.setDeliverables(new HashSet<>());
        target.setEpics(new HashSet<>());
    }

}
