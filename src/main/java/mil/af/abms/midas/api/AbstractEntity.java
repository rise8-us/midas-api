package mil.af.abms.midas.api;

import static mil.af.abms.midas.config.security.websocket.WebSocketConfig.MESSAGE_TOPIC;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessageSendingOperations;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.config.SpringContext;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity<D extends AbstractDTO> implements Serializable {

    protected static SimpMessageSendingOperations websocket() {
        return SpringContext.getBean(SimpMessageSendingOperations.class);
    }

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

    public <E extends AbstractEntity<?>> List<Long> getIds(List<E> entityList) {
        return entityList.stream().map(E::getId).collect(Collectors.toList());
    }

    public <E extends AbstractEntity<?>> Long getIdOrNull(E entity) {
        return entity != null ? entity.getId() : null;
    }

    public <E extends AbstractEntity<D>, D extends AbstractDTO> D getDtoOrNull(E entity) {
        return (entity != null && entity.getId() != null) ? entity.toDto() : null;
    }

    @Override
    @SneakyThrows
    public String toString() {
        return JsonMapper.dateMapper().writeValueAsString(this.toDto());
    }

    public abstract D toDto();

    public String getLowercaseClassName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @PostUpdate
    @PostPersist
    public void postSave() {
        var endpoint = String.format("%s/update_%s", MESSAGE_TOPIC, getLowercaseClassName());
        websocket().convertAndSend(endpoint, this.toDto());
    }

    @PostRemove
    public void postRemove() {
        var endpoint = String.format("%s/delete_%s", MESSAGE_TOPIC, getLowercaseClassName());
        websocket().convertAndSend(endpoint, this.toDto());
    }

}
