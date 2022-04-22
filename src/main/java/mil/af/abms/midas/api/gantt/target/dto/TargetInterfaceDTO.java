package mil.af.abms.midas.api.gantt.target.dto;

import mil.af.abms.midas.api.gantt.GanttDateInterfaceDTO;

public interface TargetInterfaceDTO extends GanttDateInterfaceDTO {
    public Long getParentId();
}
