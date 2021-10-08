package mil.af.abms.midas.api.roadmap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractEntity;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.roadmap.dto.RoadmapDTO;
import mil.af.abms.midas.enums.RoadmapStatus;

@Entity @Getter @Setter
@Table(name = "roadmap")
public class Roadmap extends AbstractEntity<RoadmapDTO> {

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'FUTURE'", nullable = false)
    private RoadmapStatus status = RoadmapStatus.FUTURE;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "INT")
    private Integer position;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(columnDefinition = "DATETIME", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime targetDate;

    public RoadmapDTO toDto() {
        return new RoadmapDTO(id, title, status, creationDate, description, getIdOrNull(product), position, targetDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Roadmap that = (Roadmap) o;
        return this.hashCode() == that.hashCode();
    }
}
