package mil.af.abms.midas.api.tag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.tag.dto.TagDTO;

@Entity @Getter @Setter
@Table(name = "tags")
public class Tag extends AbstractEntity<TagDTO> {

    @NaturalId(mutable = true)
    @Column(columnDefinition = "TINYTEXT", nullable = false, unique = true)
    private String label;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(columnDefinition = "TINYTEXT default '#969696'")
    private String color;

    @ManyToMany()
    @JoinTable(
        name = "project_tags",
        joinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true),
        inverseJoinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Project> projects = new HashSet<>();

    public TagDTO toDto() {
        return new TagDTO(id, label, description, color);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag that = (Tag) o;
        return this.hashCode() == that.hashCode();
    }
}
