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
public class CustomPropertyTest {

    @Autowired
    CustomProperty property;

    @Test
    public void should_return_application_yml_value() {
        assertThat(property.getVersion()).isEqualTo("0.0.1");
        assertThat(property.getClassification()).isEqualTo("UNCLASS");
        assertThat(property.getCaveat()).isEqualTo("IL2");
        assertThat(property.getMattermostToken()).isEqualTo("testToken");
        assertThat(property.getMattermostUrl()).isEqualTo("http://mattermost.foo");
        assertThat(property.getEnvironment()).isEqualTo("local");
    }
}
