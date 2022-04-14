package mil.af.abms.midas.api.gantt.event.dto;

import java.time.LocalDate;
import java.util.Set;

import mil.af.abms.midas.api.gantt.GanttInterfaceDTO;

public interface EventInterfaceDTO extends GanttInterfaceDTO {
    public LocalDate getStartDate();
    public String getLocation();
    public Set<Long> getOrganizerIds();
}
