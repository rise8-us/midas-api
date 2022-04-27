package mil.af.abms.midas.api.gantt.target;

import javax.transaction.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.gantt.GanttDateInterfaceDTO;
import mil.af.abms.midas.api.gantt.target.dto.CreateTargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.TargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.UpdateTargetDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.PortfolioService;

@Service
public class TargetService extends AbstractCRUDService<Target, TargetDTO, TargetRepository> {

    private PortfolioService portfolioService;
    private final SimpMessageSendingOperations websocket;
    private EpicService epicService;

    public TargetService(TargetRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Target.class, TargetDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setEpicService(EpicService epicService) {
        this.epicService = epicService;
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

        linkEpics(dto.getGitlabEpicIds(), newTarget);

        updateCommonFields(dto, newTarget);

        var targetCreated = repository.save(newTarget);
        sendParentUpdatedWebsocketMessage(targetCreated, true);

        return targetCreated;
    }

    @Transactional
    public Target updateById(Long id, UpdateTargetDTO dto) {
        Target foundTarget = findById(id);

        removeLinkedEpics(foundTarget);
        linkEpics(dto.getGitlabEpicIds(), foundTarget);
        updateCommonFields(dto, foundTarget);

        return repository.save(foundTarget);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        var targetToDelete = findById(id);
        removeLinkedEpics(targetToDelete);
        sendParentUpdatedWebsocketMessage(targetToDelete, false);
        targetToDelete.getChildren().forEach(t -> deleteById(t.getId()));
        repository.deleteById(id);
    }

    protected void updateCommonFields(GanttDateInterfaceDTO dto, Target target) {
        target.setStartDate(dto.getStartDate());
        target.setDueDate(dto.getDueDate());
        target.setTitle(dto.getTitle());
        target.setDescription(dto.getDescription());
    }

    protected void linkGitlabEpic(Long epicId, Target target) {
        Epic foundEpic = epicService.findByIdOrNull(epicId);

        Optional.ofNullable(foundEpic).ifPresentOrElse(epic -> {
            Epic updatedEpic = epicService.updateById(foundEpic.getId());

            Set<Epic> newEpicSet = new HashSet<>();
            newEpicSet.addAll(target.getEpics());
            newEpicSet.add(updatedEpic);
            target.setEpics(newEpicSet);

        }, () -> target.setEpics(Set.of()));
    }

    private void sendParentUpdatedWebsocketMessage(Target target, boolean isAdded) {
        Optional.ofNullable(target.getParent()).ifPresent(parent -> {
            if (isAdded) { parent.getChildren().add(target); }
            else {
                parent.setChildren(parent.getChildren().stream()
                        .filter(a -> !a.getId().equals(target.getId()))
                        .collect(Collectors.toSet())
                );
            }
            websocket.convertAndSend("/topic/update_target", parent.toDto());
        });
    }

    private void linkEpics(Set<Long> epicIds, Target newTarget) {
        Optional.ofNullable(epicIds).ifPresent(ids -> {
            ids.forEach(epicId -> {
                linkGitlabEpic(epicId, newTarget);
            });
        });
    }

    private void removeLinkedEpics(Target target) {
        target.setEpics(Set.of());
    }

}
