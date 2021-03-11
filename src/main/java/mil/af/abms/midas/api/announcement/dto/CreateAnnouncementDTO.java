package mil.af.abms.midas.api.announcement.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CreateAnnouncementDTO {

    @NotBlank(message = "announcement message must not be blank")
    private String message;
}
