package mil.af.abms.midas.api.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class SprintDateHelper {

    public static List<LocalDate> getAllSprintDates(LocalDate currentDate, int duration, int sprints) {
        List<LocalDate> allDates = new ArrayList<>(List.of(currentDate));

        for (int i = 0; i < sprints - 1; i++) {
            currentDate = currentDate.minusDays(duration);
            allDates.add(currentDate);
        }

        return allDates;
    }
}
