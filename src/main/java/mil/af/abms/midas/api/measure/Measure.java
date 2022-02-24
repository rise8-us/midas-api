package mil.af.abms.midas.api.measure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.Commentable;
import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.completion.Completion;
import mil.af.abms.midas.api.measure.dto.MeasureDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@Entity @Setter @Getter
@Table(name = "measure")
public class Measure extends AbstractEntity<MeasureDTO> implements Commentable {

    @Column(columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'NOT_STARTED'", nullable = false)
    private ProgressionStatus status = ProgressionStatus.NOT_STARTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assertion_id")
    private Assertion assertion;

    @OneToMany(mappedBy = "measure", orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(
            name = "completion_measure",
            joinColumns = @JoinColumn(name = "measure_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "completion_id", referencedColumnName = "id"))
    private Completion completion;

    public MeasureDTO toDto() {
        return new MeasureDTO(
                id,
                creationDate,
                text,
                getIdOrNull(assertion),
                getIds(comments),
                status,
                completion.toDto()
        );
    }

}
