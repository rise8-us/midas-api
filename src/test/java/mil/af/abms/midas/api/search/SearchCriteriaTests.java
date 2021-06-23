package mil.af.abms.midas.api.search;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

public class SearchCriteriaTests {

    @Test
    public void should_get_fields() {
        SearchCriteria criteria = new SearchCriteria("user.username", ":", null, "yoda", null);

        assertThat(criteria.getKey()).isEqualTo("user.username");
        assertThat(criteria.getOperation()).isEqualTo(SearchOperation.EQUALS);
        assertThat(criteria.getValue()).isEqualTo("yoda");
    }

    @Test
    public void should_skip_op() {
        SearchCriteria criteria = new SearchCriteria("user.username", "#", null, "yoda", null);

        assertThat(criteria.getKey()).isEqualTo("user.username");
        assertThat(criteria.getOperation()).isEqualTo(null);
        assertThat(criteria.getValue()).isEqualTo("yoda");
    }

    @Test
    public void should_operation_to_enum() {
        for (String op : SearchOperation.simpleOperations.keySet()) {
            SearchCriteria criteria = new SearchCriteria("user", op, null, "yoda", null);

            assertThat(criteria.getOperation()).isEqualTo(SearchOperation.getSimpleOperation(op));
        }
    }

    @Test
    public void should_handle_operation_equals_prefix_suffix() {
        SearchCriteria criteriaStartWith = new SearchCriteria("user", ":", "*", "yoda", null);
        SearchCriteria criteriaContains = new SearchCriteria("user", ":", "*", "yoda", "*");
        SearchCriteria criteriaEndsWith = new SearchCriteria("user", ":", null, "yoda", "*");

        assertThat(criteriaStartWith.getOperation()).isEqualTo(SearchOperation.ENDS_WITH);
        assertThat(criteriaContains.getOperation()).isEqualTo(SearchOperation.CONTAINS);
        assertThat(criteriaEndsWith.getOperation()).isEqualTo(SearchOperation.STARTS_WITH);
    }

    @Test
    public void should_handle_operation_not_equals_prefix_suffix() {
        SearchCriteria criteriaStartWith = new SearchCriteria("user", "!", "*", "yoda", null);
        SearchCriteria criteriaContains = new SearchCriteria("user", "!", "*", "yoda", "*");
        SearchCriteria criteriaEndsWith = new SearchCriteria("user", "!", null, "yoda", "*");

        assertThat(criteriaStartWith.getOperation()).isEqualTo(SearchOperation.DOESNT_END_WITH);
        assertThat(criteriaContains.getOperation()).isEqualTo(SearchOperation.DOESNT_CONTAIN);
        assertThat(criteriaEndsWith.getOperation()).isEqualTo(SearchOperation.DOESNT_START_WITH);
    }

}
