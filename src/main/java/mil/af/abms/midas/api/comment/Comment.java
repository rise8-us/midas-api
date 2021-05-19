package mil.af.abms.midas.api.comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.comment.dto.CommentDTO;
import mil.af.abms.midas.api.user.User;

@Entity @Getter @Setter
@Table(name = "comment")
public class Comment extends AbstractEntity<CommentDTO> {

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assertion_id")
    private Assertion assertion;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private Set<Comment> children = new HashSet<>();

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime lastEdit;

    public CommentDTO toDto() {
        return new CommentDTO(
                id,
                createdBy.toDto(),
                getIdOrNull(parent),
                getIdOrNull(assertion),
                text,
                getIds(children),
                creationDate,
                lastEdit);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment that = (Comment) o;
        return this.hashCode() == that.hashCode();
    }

}
