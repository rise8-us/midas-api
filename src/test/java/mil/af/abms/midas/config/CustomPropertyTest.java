package mil.af.abms.midas.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(CustomProperty.class)
@TestPropertySource(locations = "classpath:customPropertyTest.properties")
class CustomPropertyTest {

    @Autowired
    CustomProperty property;

    @Test
    void should_return_application_yml_value() {
        assertThat(property.getClassification()).isEqualTo("UNCLASS");
        assertThat(property.getCaveat()).isEqualTo("IL2");
        assertThat(property.getEnvironment()).isEqualTo("local");
        assertThat(property.getKey()).isEqualTo("secret-key-12345");
    }
}
