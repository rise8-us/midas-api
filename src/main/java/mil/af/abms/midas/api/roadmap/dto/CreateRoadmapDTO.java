package mil.af.abms.midas.api.roadmap.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.enums.RoadmapStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateRoadmapDTO implements Serializable {

    private String title;
    private String description;
    private Long productId;
    private RoadmapStatus status;
    private Integer index;
    private String targetDate;
}
