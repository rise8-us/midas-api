package mil.af.abms.midas.api.gantt.event.dto;

import java.util.Set;

import mil.af.abms.midas.api.gantt.GanttDateInterfaceDTO;

public interface EventInterfaceDTO extends GanttDateInterfaceDTO {
    public String getLocation();
    public Set<Long> getOrganizerIds();
    public Set<Long> getAttendeeIds();
}
