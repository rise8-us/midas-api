package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ProductTypeTests {

    @Test
    public void should_have_2_values() {
        assertThat(ProductType.values().length).isEqualTo(2);
    }

    @Test
    public void should_get_fields() {
        assertThat(ProductType.APPLICATION.getName()).isEqualTo("APPLICATION");
        assertThat(ProductType.APPLICATION.getDescription()).isEqualTo("A collection of projects such as a api and ui");
        assertThat(ProductType.APPLICATION.getLabel()).isEqualTo("Application");
    }
}
