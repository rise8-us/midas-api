package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SonarQubeEnumTests {

    @Test
    public void should_have_six_security_values() {
        assertThat(SonarqubeSecurity.values().length).isEqualTo(6);
    }

    @Test
    public void should_have_six_reliability_values() {
        assertThat(SonarqubeReliability.values().length).isEqualTo(6);
    }

    @Test
    public void should_have_six_maintainability_values() {
        assertThat(SonarqubeMaintainability.values().length).isEqualTo(6);
    }

    @Test
    public void should_get_security_fields() {
        assertThat(SonarqubeSecurity.U.getName()).isEqualTo("U");
        assertThat(SonarqubeSecurity.U.getDescription()).isEqualTo("No Data");
    }

    @Test
    public void should_get_reliability_fields() {
        assertThat(SonarqubeReliability.U.getName()).isEqualTo("U");
        assertThat(SonarqubeReliability.U.getDescription()).isEqualTo("No Data");
    }

    @Test
    public void should_get_maintainability_fields() {
        assertThat(SonarqubeMaintainability.U.getName()).isEqualTo("U");
        assertThat(SonarqubeMaintainability.U.getDescription()).isEqualTo("No Data");
    }

}
