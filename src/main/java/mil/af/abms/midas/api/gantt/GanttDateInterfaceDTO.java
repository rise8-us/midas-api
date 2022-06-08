package mil.af.abms.midas.api.gantt;

import java.time.LocalDate;

public interface GanttDateInterfaceDTO extends GanttInterfaceDTO {
    LocalDate getStartDate();
}
