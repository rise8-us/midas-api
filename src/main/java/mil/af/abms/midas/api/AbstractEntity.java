package mil.af.abms.midas.api;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import mil.af.abms.midas.api.helper.JsonMapper;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity<D extends AbstractDTO> implements Serializable {

    @Id
    @GeneratedValue
    protected Long id;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime creationDate = LocalDateTime.now();

    public <E extends AbstractEntity<?>> Set<Long> getIds(Set<E> entitySet) {
        return entitySet.stream().map(E::getId).collect(Collectors.toSet());
    }

    public <E extends AbstractEntity<?>> Long getIdOrNull(E entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    @SneakyThrows
    public String toString() {
        return JsonMapper.dateMapper().writeValueAsString(this.toDto());
    }

    public abstract D toDto();

}
