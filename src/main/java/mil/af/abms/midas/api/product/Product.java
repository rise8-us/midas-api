package mil.af.abms.midas.api.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.user.User;

@Entity @Getter @Setter
@Table(name = "products")
public class Product extends AbstractEntity<ProductDTO> {

    @Column(columnDefinition = "VARCHAR(70)", nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Column(columnDefinition = "TEXT")
    private String visionStatement;

    @Column(columnDefinition = "TEXT")
    private String problemStatement;

    @ManyToOne
    @JoinColumn(name = "product_manager_id")
    private User productManager;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @OneToMany(mappedBy = "product")
    private Set<Project> projects = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "products_tags",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Tag> tags = new HashSet<>();

    public ProductDTO toDto() {
        Long productManagerId = productManager != null ? productManager.getId() : null;
        Long portfolioId = portfolio != null ? portfolio.getId() : null;
        return new ProductDTO(id, productManagerId, portfolioId, name, description, visionStatement, problemStatement,
                isArchived, creationDate, getProjectIds(), getTagIds());
    }

    private Set<Long> getProjectIds() { return projects.stream().map(Project::getId).collect(Collectors.toSet()); }

    private Set<Long> getTagIds() { return tags.stream().map(Tag::getId).collect(Collectors.toSet()); }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product that = (Product) o;
        return this.hashCode() == that.hashCode();
    }

}
