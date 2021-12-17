package mil.af.abms.midas.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.EnumDTO;

@AllArgsConstructor
@Getter
public enum UserType {

    ACTIVE(
            "ACTIVE",
            "Active",
            "Active user"
    ),
    BOT(
            "BOT",
            "Bot",
            "External non-user entity"
    ),
    DISABLED(
            "DISABLED",
            "Disabled",
            "Disabled user"
    ),
    SYSTEM(
            "SYSTEM",
            "System",
            "Internal non-user entity"
    );

    private final String name;
    private final String displayName;
    private final String description;

    public static Stream<UserType> stream() {
        return Stream.of(UserType.values());
    }

    public static List<EnumDTO> toDTO() {
        return stream().map(u -> new EnumDTO(u.name, u.displayName, u.description)).collect(Collectors.toList());
    }
}
