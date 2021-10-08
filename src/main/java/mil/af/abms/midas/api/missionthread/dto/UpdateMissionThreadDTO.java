package mil.af.abms.midas.api.missionthread.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMissionThreadDTO implements Serializable {

    @NotBlank(message = "title must not be empty")
    @NotNull(message = "title must not be null")
    private String title;

}
