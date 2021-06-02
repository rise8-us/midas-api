package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SonarQubeEnumTests {

    @Test
    public void should_have_six_security_values() {
        assertThat(SonarQubeSecurity.values().length).isEqualTo(6);
    }

    @Test
    public void should_have_six_reliability_values() {
        assertThat(SonarQubeReliability.values().length).isEqualTo(6);
    }

    @Test
    public void should_have_six_maintainability_values() {
        assertThat(SonarQubeMaintainability.values().length).isEqualTo(6);
    }

    @Test
    public void should_get_security_fields() {
        assertThat(SonarQubeSecurity.U.getName()).isEqualTo("U");
        assertThat(SonarQubeSecurity.U.getDescription()).isEqualTo("No Data");
    }

    @Test
    public void should_get_reliability_fields() {
        assertThat(SonarQubeReliability.U.getName()).isEqualTo("U");
        assertThat(SonarQubeReliability.U.getDescription()).isEqualTo("No Data");
    }

    @Test
    public void should_get_maintainability_fields() {
        assertThat(SonarQubeMaintainability.U.getName()).isEqualTo("U");
        assertThat(SonarQubeMaintainability.U.getDescription()).isEqualTo("No Data");
    }

}
