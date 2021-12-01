package mil.af.abms.midas.api.roadmap.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.enums.RoadmapStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateRoadmapDTO implements Serializable {

    @NotBlank(message = "Please enter a roadmap title")
    private String title;
    private String description;
    private Long productId;
    private RoadmapStatus status;
    private String startDate;
    private String dueDate;
}
