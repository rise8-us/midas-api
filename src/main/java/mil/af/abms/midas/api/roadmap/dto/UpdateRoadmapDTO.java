package mil.af.abms.midas.api.roadmap.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.enums.RoadmapStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoadmapDTO implements Serializable {

    @NotBlank(message = "Please enter a roadmap title")
    private String title;
    private String description;
    private RoadmapStatus status;
    private Integer index;
    private Long id;
    @NotBlank(message = "Please enter a targeted completion date")
    private String targetDate;

}
