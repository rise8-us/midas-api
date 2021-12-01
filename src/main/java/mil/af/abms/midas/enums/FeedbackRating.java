package mil.af.abms.midas.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.FeedbackRatingDTO;

@AllArgsConstructor
@Getter
public enum FeedbackRating {

    UNSATISFACTORY(
            "UNSATISFACTORY",
            "Unsatisfactory",
            "Does not perform the task required",
            -2
    ),
    SATISFACTORY(
            "SATISFACTORY",
            "Satisfactory",
            "Needs improvement",
            -1
    ),
    AVERAGE(
            "AVERAGE",
            "Average",
            "Fulfills required task",
            0
    ),
    GOOD(
            "GOOD",
            "Good",
            "Works well",
            1
    ),
    EXCELLENT(
            "EXCELLENT",
            "Excellent",
            "Textbook definition of perfection",
            2
    );

    private final String name;
    private final String displayName;
    private final String description;
    private final Integer value;

    public static Stream<FeedbackRating> stream() {
        return Stream.of(FeedbackRating.values());
    }

    public static List<FeedbackRatingDTO> toDTO() {
        return stream().map(f -> new FeedbackRatingDTO(f.name, f.displayName, f.description, f.value)).collect(Collectors.toList());
    }
}
