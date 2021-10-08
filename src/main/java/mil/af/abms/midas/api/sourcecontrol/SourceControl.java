package mil.af.abms.midas.api.sourcecontrol;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.helper.AttributeEncryptor;
import mil.af.abms.midas.api.sourcecontrol.dto.SourceControlDTO;

@Entity @Setter @Getter
@Table(name = "source_control")
public class SourceControl extends AbstractEntity<SourceControlDTO> {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(unique = true, nullable = false)
    private String baseUrl;

    @Convert(converter = AttributeEncryptor.class)
    private String token;

    @Override
    public SourceControlDTO toDto() {
        return new SourceControlDTO(id, name, description, baseUrl, creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(baseUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceControl that = (SourceControl) o;
        return this.hashCode() == that.hashCode();
    }


}
