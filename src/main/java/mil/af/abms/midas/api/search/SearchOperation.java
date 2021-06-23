package mil.af.abms.midas.api.search;

import java.util.Map;

import lombok.Getter;

public enum SearchOperation {

    EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUAL,
    LESS_THAN, LESS_THAN_OR_EQUAL, STARTS_WITH, ENDS_WITH,
    CONTAINS, DOESNT_START_WITH, DOESNT_END_WITH, DOESNT_CONTAIN, INCLUDES;

    public static final String ZERO_OR_MORE_REGEX = "*";
    public static final String OR_OPERATOR = "OR";
    public static final String AND_OPERATOR = "AND";
    public static final String LEFT_PARENTHESIS = "(";
    public static final String RIGHT_PARENTHESIS = ")";
    @Getter
    protected static final Map<String, SearchOperation> simpleOperations = Map.ofEntries(
            Map.entry(":", EQUALS),
            Map.entry("!", NOT_EQUALS),
            Map.entry(">", GREATER_THAN),
            Map.entry(">=", GREATER_THAN_OR_EQUAL),
            Map.entry("<", LESS_THAN),
            Map.entry("<=", LESS_THAN_OR_EQUAL),
            Map.entry("::", INCLUDES)
    );

    public static SearchOperation getSimpleOperation(final String input) {
        return simpleOperations.getOrDefault(input, null);
    }

}
