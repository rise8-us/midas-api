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

    protected void linkGitlabEpic(Long gitlabEpicId, Completion completion) {
        Epic foundGitlabEpic = epicService.findByIdOrNull(gitlabEpicId);

        Optional.ofNullable(foundGitlabEpic).ifPresentOrElse(gitlabEpic -> {
            completion.setEpic(gitlabEpic);
            completion.setValue(gitlabEpic.getCompletedWeight().floatValue());
            completion.setTarget(gitlabEpic.getTotalWeight().floatValue());
            completion.setStartDate(gitlabEpic.getStartDate());
            completion.setDueDate(gitlabEpic.getDueDate());
            completion.setCompletedAt(gitlabEpic.getCompletedAt());
        }, () -> { completion.setEpic(null); });

    }

    protected void linkGitlabIssue(Long gitlabIssueId, Completion completion) {
        Issue foundGitlabIssue = issueService.findByIdOrNull(gitlabIssueId);

        Optional.ofNullable(foundGitlabIssue).ifPresentOrElse(gitlabIssue -> {
            completion.setIssue(gitlabIssue);
            completion.setTarget(gitlabIssue.getWeight().floatValue());
            completion.setStartDate(gitlabIssue.getStartDate());
            completion.setDueDate(gitlabIssue.getDueDate());
            completion.setCompletedAt(gitlabIssue.getCompletedAt());

            if (gitlabIssue.getCompletedAt() != null) {
                completion.setValue(gitlabIssue.getWeight().floatValue());
            } else {
                completion.setValue(0F);
            }
        }, () -> { completion.setIssue(null); });
    }
}
