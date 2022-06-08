package mil.af.abms.midas.api.gantt.target.dto;

import mil.af.abms.midas.api.gantt.GanttDateInterfaceDTO;

public interface TargetInterfaceDTO extends GanttDateInterfaceDTO {
    String getTitle();
    String getDescription();
    Boolean getIsPriority();
}
