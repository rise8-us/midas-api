package mil.af.abms.midas.api.search;

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
    private static final String[] SIMPLE_OPERATION_SET = {":", "!", ">", ">=", "<", "<=", "::"};

    public static SearchOperation getSimpleOperation(final String input) {
        switch (input) {
            case ":":
                return EQUALS;
            case "!":
                return NOT_EQUALS;
            case ">":
                return GREATER_THAN;
            case ">=":
                return GREATER_THAN_OR_EQUAL;
            case "<":
                return LESS_THAN;
            case "<=":
                return LESS_THAN_OR_EQUAL;
            case "::":
                return INCLUDES;
            default:
                return null;
        }
    }

}
