package mil.af.abms.midas.api.coverage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.coverage.dto.CoverageDTO;

@RestController
@RequestMapping("/api/Coverages")
public class CoverageController extends AbstractCRUDController<Coverage, CoverageDTO, CoverageService> {

    @Autowired
    public CoverageController(CoverageService service) {
        super(service);
    }

    @GetMapping("/project/{projectId}")
    public CoverageDTO getCoverage(@PathVariable Long projectId) {
        return service.updateCoverageForProjectById(projectId).toDto();
    }

}
