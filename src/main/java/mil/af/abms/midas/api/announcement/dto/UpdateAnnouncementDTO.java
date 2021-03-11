package mil.af.abms.midas.api.announcement.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateAnnouncementDTO {

    @NotBlank(message = "announcement message must not be blank")
    private String message;
}
