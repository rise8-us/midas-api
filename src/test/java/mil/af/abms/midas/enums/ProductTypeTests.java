package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProductTypeTests {

    @Test
    void should_have_2_values() {
        assertThat(ProductType.values().length).isEqualTo(2);
    }

    @Test
    void should_get_fields() {
        assertThat(ProductType.PRODUCT.getName()).isEqualTo("PRODUCT");
        assertThat(ProductType.PRODUCT.getDescription()).isEqualTo("A collection of projects such as a api and ui");
        assertThat(ProductType.PRODUCT.getLabel()).isEqualTo("Product");
    }
}
