package mil.af.abms.midas.api.measure.dto;

import mil.af.abms.midas.enums.CompletionType;

public interface MeasurableDTO {
    public Float getTarget();
    public Float getValue();
    public CompletionType getCompletionType();
}
