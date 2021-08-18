package mil.af.abms.midas.api.roadmap.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.enums.RoadmapStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoadmapDTO implements Serializable {

    private String title;
    private String description;
    private RoadmapStatus status;
    private Integer index;
    private Long id;
    private String targetDate;

}
