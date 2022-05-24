package mil.af.abms.midas.api.completion;

import static mil.af.abms.midas.api.helper.TimeConversion.getLocalDateOrNullFromObject;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.completion.dto.CompletionDTO;
import mil.af.abms.midas.api.completion.dto.CreateCompletionDTO;
import mil.af.abms.midas.api.completion.dto.UpdateCompletionDTO;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.issue.Issue;
import mil.af.abms.midas.api.issue.IssueService;
import mil.af.abms.midas.api.measure.dto.MeasurableDTO;
import mil.af.abms.midas.enums.CompletionType;

@Slf4j
@Service
public class CompletionService extends AbstractCRUDService<Completion, CompletionDTO, CompletionRepository> {

    private EpicService epicService;
    private IssueService issueService;

    public CompletionService(CompletionRepository repository) {
        super(repository, Completion.class, CompletionDTO.class);
    }

    @Autowired
    public void setIssueService(IssueService issueService) {
        this.issueService = issueService;
    }
    @Autowired
    public void setEpicService(EpicService epicService) {
        this.epicService = epicService;
    }

    public Completion create(CreateCompletionDTO dto) {
        Completion completion = Builder.build(Completion.class)
                .with(m -> m.setStartDate(getLocalDateOrNullFromObject(dto.getStartDate())))
                .with(m -> m.setDueDate(getLocalDateOrNullFromObject(dto.getDueDate())))
                .get();
        updateRequiredNotNullFields(dto, completion);

        linkGitlabEpic(dto.getGitlabEpicId(), completion);
        linkGitlabIssue(dto.getGitlabIssueId(), completion);

        return repository.save(completion);
    }

    public Completion updateById(Long id, UpdateCompletionDTO dto) {
        Completion foundCompletion = findById(id);
        foundCompletion.setStartDate(getLocalDateOrNullFromObject(dto.getStartDate()));
        foundCompletion.setDueDate(getLocalDateOrNullFromObject(dto.getDueDate()));
        foundCompletion.setCompletedAt(getCompletedAtOrNull(dto.getValue(), dto.getTarget()));
        updateRequiredNotNullFields(dto, foundCompletion);

        linkGitlabEpic(dto.getGitlabEpicId(), foundCompletion);
        linkGitlabIssue(dto.getGitlabIssueId(), foundCompletion);

        return repository.save(foundCompletion);
    }

    protected void updateRequiredNotNullFields(MeasurableDTO dto, Completion foundCompletion) {
        Optional.ofNullable(dto.getCompletionType()).ifPresent(foundCompletion::setCompletionType);
        Optional.ofNullable(dto.getValue()).ifPresent(foundCompletion::setValue);
        Optional.ofNullable(dto.getTarget()).ifPresent(foundCompletion::setTarget);
    }

    protected LocalDateTime getCompletedAtOrNull(Float value, Float target) {
        return value >= target ? LocalDateTime.now() : null;
    }

    @Transactional
    public void setStartDate(Long id, LocalDate newStartDate) {
        Completion foundCompletion = findById(id);
        foundCompletion.setStartDate(newStartDate);
        repository.save(foundCompletion);
    }

    @Transactional
    public void updateTarget(Long id, Float value) {
        var foundCompletion = findByIdOrNull(id);
        foundCompletion.setTarget(foundCompletion.getTarget() + value);
        repository.save(foundCompletion);
    }

    @Transactional
    public void setCompletedAtAndValueToTarget(Long id) {
        Completion foundCompletion = findById(id);
        foundCompletion.setValue(foundCompletion.getTarget());
        foundCompletion.setCompletedAt(LocalDateTime.now());
        repository.save(foundCompletion);
    }

    @Transactional
    public void setCompletionTypeToFailure(Long id) {
        Completion foundCompletion = findById(id);
        foundCompletion.setCompletionType(CompletionType.CONNECTION_FAILURE);
        repository.save(foundCompletion);
    }

    protected void linkGitlabEpic(Long epicId, Completion completion) {
        Epic foundEpic = epicService.findByIdOrNull(epicId);

        Optional.ofNullable(foundEpic).ifPresentOrElse(epic -> {
            if (Optional.ofNullable(epic.getProduct()).isPresent()) {
                completion.setEpic(epicService.updateByIdForProduct(epic.getId()));
            }
            if (Optional.ofNullable(epic.getPortfolio()).isPresent()) {
                completion.setEpic(epicService.updateByIdForPortfolio(epic.getId()));
            }
            updateCompletionWithGitlabEpic(completion, epic);
        }, () -> completion.setEpic(null));

    }

    protected void linkGitlabIssue(Long issueId, Completion completion) {
        Issue foundIssue = issueService.findByIdOrNull(issueId);

        Optional.ofNullable(foundIssue).ifPresentOrElse(issue -> {
            Issue updatedIssue = issueService.updateById(issue.getId());
            completion.setIssue(updatedIssue);
            updateCompletionWithGitlabIssue(completion, updatedIssue);
        }, () -> completion.setIssue(null));
    }

    public void updateLinkedIssue(Issue issue) {
        issue.getCompletions().forEach(completion -> {
            updateCompletionWithGitlabIssue(completion, issue);
            repository.save(completion);
        });
    }

    public void updateLinkedEpic(Epic epic) {
        epic.getCompletions().forEach(completion -> {
            updateCompletionWithGitlabEpic(completion, epic);
            repository.save(completion);
        });
    }

    protected void updateCompletionWithGitlabEpic(Completion completion, Epic epic) {
        completion.setValue(epic.getCompletedWeight().floatValue());
        completion.setTarget(epic.getTotalWeight().floatValue());
        completion.setStartDate(epic.getStartDate());
        completion.setDueDate(epic.getDueDate());
        completion.setCompletedAt(epic.getCompletedAt());
    }

    protected void updateCompletionWithGitlabIssue(Completion completion, Issue issue) {
        completion.setTarget(issue.getWeight().floatValue());
        completion.setStartDate(issue.getStartDate());
        completion.setDueDate(issue.getDueDate());
        completion.setCompletedAt(issue.getCompletedAt());

        if (issue.getCompletedAt() != null) {
            completion.setValue(issue.getWeight().floatValue());
        } else {
            completion.setValue(0F);
        }
    }
}
