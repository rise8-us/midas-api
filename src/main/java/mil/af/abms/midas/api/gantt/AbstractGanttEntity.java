package mil.af.abms.midas.api.gantt;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.AbstractEntity;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractGanttEntity<D extends AbstractDTO> extends AbstractEntity<D> implements Serializable {

    @Column(columnDefinition = "DATE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    protected LocalDate dueDate;

    @Column(columnDefinition = "TEXT")
    protected String title;

    @Column(columnDefinition = "TEXT")
    protected String description;
}
