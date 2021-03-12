package mil.af.abms.midas.api.search;

import lombok.Getter;

public class SearchCriteria {

    @Getter
    private final String key;

    @Getter
    private final SearchOperation operation;

    @Getter
    private final String value;

    public SearchCriteria(String key, String operation, String prefix, String value, String suffix) {
        SearchOperation op = SearchOperation.getSimpleOperation(operation);
        if (op != null) {
            boolean startsWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
            boolean endsWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
            op = searchOperationHandler(op, startsWithAsterisk, endsWithAsterisk);
        }
        this.operation = op;
        this.key = key;
        this.value = value;
    }

    private SearchOperation searchOperationHandler(SearchOperation op, boolean startsWithAsterisk, boolean endsWithAsterisk) {
        if (op == SearchOperation.EQUALS && startsWithAsterisk && endsWithAsterisk) {
            op = SearchOperation.CONTAINS;
        } else if (op == SearchOperation.EQUALS && startsWithAsterisk) {
            op = SearchOperation.ENDS_WITH;
        } else if (op == SearchOperation.EQUALS && endsWithAsterisk) {
            op = SearchOperation.STARTS_WITH;
        }

        if (op == SearchOperation.NOT_EQUALS && startsWithAsterisk && endsWithAsterisk) {
            op = SearchOperation.DOESNT_CONTAIN;
        } else if (op == SearchOperation.NOT_EQUALS && startsWithAsterisk) {
            op = SearchOperation.DOESNT_END_WITH;
        } else if (op == SearchOperation.NOT_EQUALS && endsWithAsterisk) {
            op = SearchOperation.DOESNT_START_WITH;
        }
        return op;
    }

}
