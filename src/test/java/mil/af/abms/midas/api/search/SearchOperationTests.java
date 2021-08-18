package mil.af.abms.midas.api.search;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class SearchOperationTests {

    @Test
    void should_convert_operation_to_enum() {
        assertThat(SearchOperation.getSimpleOperation(":")).isEqualTo(SearchOperation.EQUALS);
        assertThat(SearchOperation.getSimpleOperation("!")).isEqualTo(SearchOperation.NOT_EQUALS);
        assertThat(SearchOperation.getSimpleOperation(">")).isEqualTo(SearchOperation.GREATER_THAN);
        assertThat(SearchOperation.getSimpleOperation(">=")).isEqualTo(SearchOperation.GREATER_THAN_OR_EQUAL);
        assertThat(SearchOperation.getSimpleOperation("<")).isEqualTo(SearchOperation.LESS_THAN);
        assertThat(SearchOperation.getSimpleOperation("<=")).isEqualTo(SearchOperation.LESS_THAN_OR_EQUAL);
        assertThat(SearchOperation.getSimpleOperation("::")).isEqualTo(SearchOperation.INCLUDES);
        assertThat(SearchOperation.getSimpleOperation(":~")).isEqualTo(SearchOperation.NULL);
        assertThat(SearchOperation.getSimpleOperation("!~")).isEqualTo(SearchOperation.NOT_NULL);
    }

    @Test
    void should_get_simple_operators() {
        assertThat(SearchOperation.simpleOperations.entrySet().size()).isEqualTo(9);
    }
}
