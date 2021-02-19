package us.rise8.mixer;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.junit.jupiter.api.Test;

import us.rise8.mixer.config.Startup;

@SpringBootTest
class mixerApiTests {

    @MockBean
    Startup startup;

    @Test
    void contextLoads() {
    }

}
