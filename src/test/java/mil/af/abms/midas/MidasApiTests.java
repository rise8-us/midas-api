package mil.af.abms.midas;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.config.CustomProperty;
import mil.af.abms.midas.config.Startup;

@SpringBootTest
@TestPropertySource(locations = "classpath:customPropertyTest.properties")
@EnableConfigurationProperties(CustomProperty.class)
class MidasApiTests {

    @MockBean
    Startup startup;

    @Test
    void contextLoads() {
    }

}
