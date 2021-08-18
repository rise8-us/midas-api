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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.AssertionStatus;
import mil.af.abms.midas.enums.AssertionType;
import mil.af.abms.midas.enums.CompletionType;

@Entity @Setter @Getter
@Table(name = "assertion")
public class Assertion extends AbstractEntity<AssertionDTO> {

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70)")
    private AssertionType type;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'NOT_STARTED'", nullable = false)
    private AssertionStatus status = AssertionStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'STRING'", nullable = false)
    private CompletionType completionType = CompletionType.STRING;

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

    @OneToMany(mappedBy = "parent")
    private Set<Assertion> children = new HashSet<>();

    @OneToMany(mappedBy = "assertion", orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime completedDate;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime startDate;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime dueDate;

    public AssertionDTO toDto() {
        return new AssertionDTO(
                id,
                getIdOrNull(product),
                getIdOrNull(createdBy),
                getIdOrNull(parent),
                text,
                type,
                status,
                getIds(comments),
                children.stream().map(Assertion::toDto).collect(Collectors.toList()),
                creationDate,
                completedDate,
                startDate,
                dueDate,
                isArchived,
                completionType,
                getIdOrNull(assignedPerson)

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
        Assertion that = (Assertion) o;
        return this.hashCode() == that.hashCode();
    }

}
