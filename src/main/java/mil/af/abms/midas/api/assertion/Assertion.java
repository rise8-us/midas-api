package mil.af.abms.midas.api.assertion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.ogsm.Ogsm;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.AssertionType;

@Entity @Setter @Getter
@Table(name = "assertion")
public class Assertion extends AbstractEntity<AssertionDTO> {

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "VARCHAR(70)")
    private AssertionType type;

    @ManyToMany
    @JoinTable(
            name = "assertion_tag",
            joinColumns = @JoinColumn(name = "assertion_id", referencedColumnName = "id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @OneToMany(mappedBy = "assertion")
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JoinColumn(columnDefinition = "ogsm_id")
    private Ogsm ogsm;

    public AssertionDTO toDto() {
        return new AssertionDTO(id, getIdOrNull(ogsm), getIdOrNull(createdBy), text, type, creationDate, getIds(tags),
                getIds(comments));
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
