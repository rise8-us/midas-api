package mil.af.abms.midas.api.completion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractTimeConstrainedEntity;
import mil.af.abms.midas.api.completion.dto.CompletionDTO;
import mil.af.abms.midas.api.deliverable.Deliverable;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.issue.Issue;
import mil.af.abms.midas.api.measure.Measure;
import mil.af.abms.midas.enums.CompletionType;

@Entity @Getter @Setter
@Table(name = "completion")
public class Completion extends AbstractTimeConstrainedEntity<CompletionDTO> {

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'BINARY'", nullable = false)
    private CompletionType completionType = CompletionType.BINARY;

    @Column(columnDefinition = "FLOAT DEFAULT 0", nullable = false)
    private Float value = 0F;

    @Column(columnDefinition = "FLOAT DEFAULT 1", nullable = false)
    private Float target = 1F;

    @OneToOne(mappedBy =  "completion", orphanRemoval = true)
    private Measure measure;

    @OneToOne(mappedBy = "completion", orphanRemoval = true)
    private Deliverable deliverable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "completion_gitlab_epic",
            joinColumns = @JoinColumn(name = "completion_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "epic_id", referencedColumnName = "id"))
    private Epic epic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "completion_gitlab_issue",
            joinColumns = @JoinColumn(name = "completion_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "issue_id", referencedColumnName = "id"))
    private Issue issue;

    public CompletionDTO toDto() {
        return new CompletionDTO(
                id,
                startDate,
                dueDate,
                completedAt,
                completionType,
                value,
                target,
                getDtoOrNull(epic),
                getDtoOrNull(issue)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Completion that = (Completion) o;
        return this.hashCode() == that.hashCode();
    }

}
