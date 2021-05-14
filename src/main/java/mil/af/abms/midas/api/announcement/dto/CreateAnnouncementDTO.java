package mil.af.abms.midas.api.announcement.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;

import lombok.Data;

@Data
public class CreateAnnouncementDTO implements Serializable {

    @NotBlank(message = "announcement message must not be blank")
    private String message;
}
