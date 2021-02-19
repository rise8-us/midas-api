package us.rise8.mixer.api;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import us.rise8.mixer.api.helper.JsonMapper;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity<D extends AbstractDTO> implements Serializable {

    @Id
    @GeneratedValue
    protected Long id;

    @Override
    @SneakyThrows
    public String toString() {
        return JsonMapper.dateMapper().writeValueAsString(this.toDto());
    }

    public abstract D toDto();

}
