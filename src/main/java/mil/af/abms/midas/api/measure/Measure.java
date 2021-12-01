package mil.af.abms.midas.api.measure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractTimeConstrainedEntity;
import mil.af.abms.midas.api.Commentable;
import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.measure.dto.MeasureDTO;
import mil.af.abms.midas.enums.CompletionType;

@Entity @Setter @Getter
@Table(name = "measure")
public class Measure extends AbstractTimeConstrainedEntity<MeasureDTO> implements Commentable {

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'BINARY'", nullable = false)
    private CompletionType completionType = CompletionType.BINARY;

    @Column(columnDefinition = "FLOAT DEFAULT 0", nullable = false)
    private Float value;

    @Column(columnDefinition = "FLOAT DEFAULT 1", nullable = false)
    private Float target;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assertion_id")
    private Assertion assertion;

    @OneToMany(mappedBy = "measure", orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    public MeasureDTO toDto() {
        return new MeasureDTO(
                id,
                creationDate,
                startDate,
                dueDate,
                completedAt,
                completionType,
                value,
                target,
                text,
                getIdOrNull(assertion),
                getIds(comments)
        );
    }

}
