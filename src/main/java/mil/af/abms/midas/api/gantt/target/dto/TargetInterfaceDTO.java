package mil.af.abms.midas.api.gantt.target.dto;

import java.time.LocalDate;

import mil.af.abms.midas.api.gantt.GanttInterfaceDTO;

public interface TargetInterfaceDTO extends GanttInterfaceDTO {
    public LocalDate getStartDate();
}
