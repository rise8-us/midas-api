package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CompletionTypeTests {

    @Test
    void should_have_7_values() {
        assertThat(CompletionType.values().length).isEqualTo(6);
    }

    @Test
    void should_get_fields() {
        assertThat(CompletionType.BINARY.getName()).isEqualTo("BINARY");
        assertThat(CompletionType.BINARY.getDisplayName()).isEqualTo("Binary");
        assertThat(CompletionType.BINARY.getDescription()).isEqualTo("Complete? true or false");
        assertThat(CompletionType.PERCENTAGE.getName()).isEqualTo("PERCENTAGE");
        assertThat(CompletionType.PERCENTAGE.getDisplayName()).isEqualTo("Percentage");
        assertThat(CompletionType.PERCENTAGE.getDescription()).isEqualTo("Percentage of completeness");
        assertThat(CompletionType.NUMBER.getName()).isEqualTo("NUMBER");
        assertThat(CompletionType.NUMBER.getDisplayName()).isEqualTo("Number");
        assertThat(CompletionType.NUMBER.getDescription()).isEqualTo("Numerical representation of completeness");
        assertThat(CompletionType.MONEY.getName()).isEqualTo("MONEY");
        assertThat(CompletionType.MONEY.getDisplayName()).isEqualTo("Money");
        assertThat(CompletionType.MONEY.getDescription()).isEqualTo("Monetary representation of completeness");
        assertThat(CompletionType.GITLAB_EPIC.getName()).isEqualTo("GITLAB_EPIC");
        assertThat(CompletionType.GITLAB_EPIC.getDisplayName()).isEqualTo("GitLab Epic");
        assertThat(CompletionType.GITLAB_EPIC.getDescription()).isEqualTo("Progress synced to a GitLab epic");
        assertThat(CompletionType.GITLAB_ISSUE.getName()).isEqualTo("GITLAB_ISSUE");
        assertThat(CompletionType.GITLAB_ISSUE.getDisplayName()).isEqualTo("GitLab Issue");
        assertThat(CompletionType.GITLAB_ISSUE.getDescription()).isEqualTo("Progress synced to a GitLab issue");
    }
}
