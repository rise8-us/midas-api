package mil.af.abms.midas.api.dtos;

import java.io.Serializable;

public interface CompletableDTO extends Serializable {
    public String getStartDate();
    public String getDueDate();
}
