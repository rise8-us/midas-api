package mil.af.abms.midas.api.appusermetrics.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserMetricsDTO implements AbstractDTO {
    private LocalDate id;
    private Long uniqueLogins;
}
