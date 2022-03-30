package mil.af.abms.midas.api.comment;

import java.time.LocalDate;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import mil.af.abms.midas.enums.ProgressionStatus;

public class SystemComments {

    private SystemComments() {
        throw new IllegalStateException("Utility Class");
    }

    public static final UnaryOperator<String> ALL_ASSERTION_CHILDREN_COMPLETE = assertionText ->
            String.format("All requirements were completed, marking \"%s\" as complete.###%s", assertionText, ProgressionStatus.COMPLETED.getName());

    public static final BinaryOperator<String> ASSERTION_COMPLETE = (assertionText, childText) ->
            String.format("Status of \"%s\" was set to complete, marking \"%s\" as complete.###%s", assertionText, childText, ProgressionStatus.COMPLETED.getName());

    public static final UnaryOperator<String> MEASURE_VALUE_MET_TARGET = measureText ->
            String.format("Value of \"%s\" was set equal to the given target, marking as complete.###%s", measureText, ProgressionStatus.COMPLETED.getName());

    public static final UnaryOperator<String> MEASURE_STATUS_SET_COMPLETED = measureText ->
            String.format("Status of \"%s\" was set to complete, value has been set equal to the given target.###%s", measureText, ProgressionStatus.COMPLETED.getName());

    public static final UnaryOperator<String> MEASURE_PAST_DUE = measureText ->
            String.format("Progress of \"%s\" was updated past the given due date without it being completed, marking as blocked.###%s", measureText, ProgressionStatus.BLOCKED.getName());

    public static final UnaryOperator<String> MEASURE_VALUE_SET_TO_ZERO = measureText ->
            String.format("Value of \"%s\" was set to zero, marking as not started.###%s", measureText, ProgressionStatus.NOT_STARTED.getName());

    public static final UnaryOperator<String> MEASURE_ON_TRACK_SET_START_DATE = measureText ->
            String.format("Progress of \"%s\" was updated within the given time range, marking as on track and setting start date to %s.###%s", measureText, LocalDate.now(), ProgressionStatus.ON_TRACK.getName());

    public static final UnaryOperator<String> MEASURE_ON_TRACK = measureText ->
            String.format("Progress of \"%s\" was updated within the given time range, marking as on track.###%s", measureText, ProgressionStatus.ON_TRACK.getName());
}
