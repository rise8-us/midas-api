package mil.af.abms.midas.api.assertion;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.config.security.annotations.HasOGSMCreateAccess;
import mil.af.abms.midas.config.security.annotations.HasOGSMUpdateAccess;

@RestController
@RequestMapping("/api/assertions")
public class AssertionController extends AbstractCRUDController<Assertion, AssertionDTO, AssertionService> {

    @Autowired
    public AssertionController(AssertionService service) {
        super(service);
    }

    @PostMapping
    @HasOGSMCreateAccess
    public AssertionDTO create(@Valid @RequestBody CreateAssertionDTO createAssertionDTO) {
        return service.create(createAssertionDTO).toDto();
    }

    @PutMapping("/{id}")
    @HasOGSMUpdateAccess
    public AssertionDTO updateById(@Valid @RequestBody UpdateAssertionDTO updateAssertionDTO, @PathVariable Long id) {
        return service.updateById(id, updateAssertionDTO).toDto();
    }

    @DeleteMapping("/{id}")
    @Override
    @HasOGSMUpdateAccess
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

}