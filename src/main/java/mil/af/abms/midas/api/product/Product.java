package mil.af.abms.midas.api.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.dto.TagDTO;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.ProductType;

@Entity @Getter @Setter
@Table(name = "product")
public class Product extends AbstractEntity<ProductDTO> {

    @Column(columnDefinition = "VARCHAR(70)", nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String vision;

    @Column(columnDefinition = "TEXT")
    private String mission;

    @Column(columnDefinition = "TEXT")
    private String problemStatement;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isArchived = false;

    @Column(columnDefinition = "INT")
    private Integer gitlabGroupId;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductType type = ProductType.PRODUCT;

    @ManyToOne
    @JoinColumn(name = "product_manager_id")
    private User productManager;

    @ManyToOne
    private SourceControl sourceControl;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    private Product parent;

    @OneToMany(mappedBy = "parent")
    private Set<Product> children = new HashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<Project> projects = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "product_tag",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "product_team",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = true)
    )
    private Set<Team> teams = new HashSet<>();

    public ProductDTO toDto() {
        Set<TagDTO> tagDTOs = tags.stream().map(Tag::toDto).collect(Collectors.toSet());
        return new ProductDTO(
                id, getIdOrNull(productManager),
                getIdOrNull(parent),
                name,
                description,
                isArchived,
                creationDate,
                getIds(projects),
                tagDTOs,
                getIds(children),
                type,
                gitlabGroupId,
                getIdOrNull(sourceControl),
                getIds(teams),
                vision,
                mission,
                problemStatement
        );
    }

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
