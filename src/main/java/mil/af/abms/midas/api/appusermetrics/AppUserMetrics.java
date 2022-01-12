package mil.af.abms.midas.api.appusermetrics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.appusermetrics.dto.AppUserMetricsDTO;

@Entity
@Setter
@Getter
@Table(name = "app_user_metrics")
public class AppUserMetrics implements Serializable {

    @Id
    @Column(columnDefinition = "DATE DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    protected LocalDate id;

    @Column(columnDefinition = "BIGINT")
    private Long uniqueLogins;

    public AppUserMetricsDTO toDto() {
        return new AppUserMetricsDTO(
                id,
                uniqueLogins
        );
    }
}
