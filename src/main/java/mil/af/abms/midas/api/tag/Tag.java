package mil.af.abms.midas.api.tag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.tag.dto.TagDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.TagType;

@Entity @Getter @Setter
@Table(name = "tag")
public class Tag extends AbstractEntity<TagDTO> {

    @NaturalId(mutable = true)
    @Column(columnDefinition = "TINYTEXT")
    private String label;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(columnDefinition = "TINYTEXT default '#969696'")
    private String color;

    @Enumerated(value = EnumType.STRING)
    private TagType tagType = TagType.ALL;

    @ManyToMany
    @JoinTable(
        name = "project_tag",
        joinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true),
        inverseJoinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Project> projects = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "product_tag",
            joinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Product> products = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    public TagDTO toDto() {
        Long createdById = createdBy != null ? createdBy.getId() : null;
        return new TagDTO(id, label, description, color, createdById, tagType);
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
