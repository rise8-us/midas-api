package mil.af.abms.midas.api.persona;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.persona.dto.PersonaDTO;
import mil.af.abms.midas.api.product.Product;

@Entity @Getter @Setter
@Table(name = "persona")
public class Persona extends AbstractEntity<PersonaDTO> {

    @Column(nullable = false, columnDefinition = "VARCHAR(70)")
    private String title;

    @Column(columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private Boolean isSupported = false;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "INT")
    private Integer position;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public PersonaDTO toDto() {
        return new PersonaDTO(id, title, isSupported, creationDate, description, getIdOrNull(product), position);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona that = (Persona) o;
        return this.hashCode() == that.hashCode();
    }
}
