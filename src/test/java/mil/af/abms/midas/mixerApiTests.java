package mil.af.abms.midas;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.config.Startup;

@SpringBootTest
class mixerApiTests {

    @MockBean
    Startup startup;

    @Test
    void contextLoads() {
    }

}
