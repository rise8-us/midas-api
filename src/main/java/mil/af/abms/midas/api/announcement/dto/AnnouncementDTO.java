package mil.af.abms.midas.api.announcement.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementDTO implements AbstractDTO {
    private long id;
    private LocalDateTime creationDate;
    private String message;
}
