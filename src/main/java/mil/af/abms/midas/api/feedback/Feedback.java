package mil.af.abms.midas.api.feedback;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import mil.af.abms.midas.api.feedback.dto.FeedbackDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.FeedbackRating;

@Entity @Setter @Getter
@Table(name = "feedback")
public class Feedback extends AbstractEntity<FeedbackDTO> {

    @Column(columnDefinition = "DATETIME", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime editedAt;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(70) DEFAULT 'AVERAGE'", nullable = false)
    private FeedbackRating rating = FeedbackRating.AVERAGE;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String relatedTo;

    @ManyToOne
    @JoinColumn(name = "edited_by_id", nullable = true)
    private User editedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    public FeedbackDTO toDto() {
        return new FeedbackDTO(
            id,
            creationDate,
            getIdOrNull(createdBy),
            getIdOrNull(editedBy),
            editedAt,
            rating,
            notes,
            relatedTo
        );
    }

}
