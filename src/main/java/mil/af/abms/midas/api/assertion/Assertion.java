package mil.af.abms.midas.api.assertion;

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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractTimeConstrainedEntity;
import mil.af.abms.midas.api.Commentable;
import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.measure.Measure;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.ProgressionStatus;

@Entity @Setter @Getter
@Table(name = "assertion")
public class Assertion extends AbstractTimeConstrainedEntity<AssertionDTO> implements Commentable {

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'NOT_STARTED'", nullable = false)
    private ProgressionStatus status = ProgressionStatus.NOT_STARTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_person_id")
    private User assignedPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Assertion parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inherited_from")
    private Assertion inheritedFrom;

    @OneToMany(mappedBy = "assertion")
    private Set<Measure> measures = new HashSet<>();

    @OneToMany(mappedBy = "parent")
    private Set<Assertion> children = new HashSet<>();

    @OneToMany(mappedBy = "inheritedFrom")
    private Set<Assertion> passedTo = new HashSet<>();

    @OneToMany(mappedBy = "assertion", orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    public AssertionDTO toDto() {
        return new AssertionDTO(
                id,
                getIdOrNull(product),
                getIdOrNull(createdBy),
                getIdOrNull(parent),
                getIdOrNull(inheritedFrom),
                text,
                status,
                getIds(comments),
                measures.stream().map(Measure::getId).collect(Collectors.toList()),
                children.stream().map(Assertion::toDto).collect(Collectors.toList()),
                passedTo.stream().map(Assertion::getId).collect(Collectors.toList()),
                creationDate,
                startDate,
                dueDate,
                completedAt,
                isArchived,
                getIdOrNull(assignedPerson)
        );
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assertion that = (Assertion) o;
        return this.hashCode() == that.hashCode();
    }

}
