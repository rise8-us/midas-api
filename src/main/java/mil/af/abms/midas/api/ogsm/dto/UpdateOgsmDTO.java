package mil.af.abms.midas.api.ogsm.dto;

import javax.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOgsmDTO {

    @NotEmpty(message = "Objective cannot be blank")
    private String text;

    private LocalDateTime completedDate;

}
