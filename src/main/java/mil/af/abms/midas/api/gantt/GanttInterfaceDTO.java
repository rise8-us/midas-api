package mil.af.abms.midas.api.gantt;

import java.io.Serializable;
import java.time.LocalDate;

public interface GanttInterfaceDTO extends Serializable {
    String getTitle();
    String getDescription();
    LocalDate getDueDate();
}
