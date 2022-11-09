package mil.af.abms.midas.api.epic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import mil.af.abms.midas.api.AbstractTimeConstrainedEntity;
import mil.af.abms.midas.api.completion.Completion;
import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.product.Product;

@Entity @Setter @Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "epic")
public class Epic extends AbstractTimeConstrainedEntity<EpicDTO> {

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "BIT(1) DEFAULT 0")
    private Boolean isHidden;

    @Column(columnDefinition = "DATE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDateFromInheritedSource;

    @Column(columnDefinition = "DATE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dueDateFromInheritedSource;

    @Column(columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime syncedAt;

    @Column(columnDefinition = "INT")
    private Integer epicIid;

    @Column(columnDefinition = "TEXT")
    private String state;

    @Column(columnDefinition = "TEXT")
    private String webUrl;

    @Column(columnDefinition = "VARCHAR(255)")
    private String epicUid;

    /**
     * @deprecated to reduce number of calls to Gitlab and remove progress bars
     * May be updated for future use
     */
    @Deprecated
    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long totalWeight = 0L;

    /**
     * @deprecated to reduce number of calls to Gitlab and remove progress bars
     * May be updated for future use
     */
    @Deprecated
    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long completedWeight = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @OneToMany
    @JoinTable(
            name = "completion_gitlab_epic",
            joinColumns = @JoinColumn(name = "epic_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "completion_id", referencedColumnName = "id"))
    private Set<Completion> completions;

    public EpicDTO toDto() {
        return new EpicDTO(
            id,
            title,
            description,
            isHidden,
            creationDate,
            startDate,
            startDateFromInheritedSource,
            dueDate,
            dueDateFromInheritedSource,
            completedAt,
            syncedAt,
            epicIid,
            state,
            webUrl,
            epicUid,
            getIdOrNull(product),
            getIdOrNull(portfolio)
        );
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic that = (Epic) o;
        return this.hashCode() == that.hashCode();
    }

}
