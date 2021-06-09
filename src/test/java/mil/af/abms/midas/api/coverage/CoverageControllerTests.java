package mil.af.abms.midas.api.coverage;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.assertion.AssertionController;

@WebMvcTest({AssertionController.class})
public class CoverageControllerTests extends ControllerTestHarness {
    
    @MockBean
    private CoverageService coverageService;

}
