package mil.af.abms.midas.api.gitlabconfig;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.gitlabconfig.dto.GitlabConfigDTO;
import mil.af.abms.midas.api.helper.AttributeEncryptor;

@Entity @Setter @Getter
@Table(name = "gitlab_config")
public class GitlabConfig extends AbstractEntity<GitlabConfigDTO> {


    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(unique = true, nullable = false)
    private String baseUrl;

    @Convert(converter = AttributeEncryptor.class)
    private String token;

    @Override
    public GitlabConfigDTO toDto() {
        return new GitlabConfigDTO(id, name, description, baseUrl, creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(baseUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitlabConfig that = (GitlabConfig) o;
        return this.hashCode() == that.hashCode();
    }


}
