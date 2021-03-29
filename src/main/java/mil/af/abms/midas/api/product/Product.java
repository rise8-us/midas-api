package mil.af.abms.midas.api.product;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.team.Team;

@Entity @Getter @Setter
@Table(name = "products")
public class Product extends AbstractEntity<ProductDTO> {

    @Column(columnDefinition = "VARCHAR(70)", nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "BIT(1) DEFAULT 0")
    private Boolean isArchived = false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(columnDefinition = "BIGINT", nullable = false)
    private Long gitlabProjectId;

    public ProductDTO toDto() {
        Long teamId = null;

        if (team != null) {
            teamId = team.getId();
        }

        return new ProductDTO(id, name, description, isArchived, creationDate, gitlabProjectId, getTagIds(), teamId);
    }

    private Set<Long> getTagIds() { return tags.stream().map(Tag::getId).collect(Collectors.toSet()); }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product that = (Product) o;
        return this.hashCode() == that.hashCode();
    }
}
