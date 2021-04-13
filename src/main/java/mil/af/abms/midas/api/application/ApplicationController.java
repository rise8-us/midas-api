package mil.af.abms.midas.api.application;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.application.dto.CreateApplicationDTO;
import mil.af.abms.midas.api.application.dto.ApplicationDTO;
import mil.af.abms.midas.api.application.dto.UpdateApplicationDTO;
import mil.af.abms.midas.api.application.dto.UpdateApplicationIsArchivedDTO;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController extends AbstractCRUDController<Application, ApplicationDTO, ApplicationService> {

    @Autowired
    public ApplicationController(ApplicationService service) {
        super(service);
    }

    @PostMapping
    public ApplicationDTO create(@Valid @RequestBody CreateApplicationDTO applicationDTO) {
        return service.create(applicationDTO).toDto();
    }

    @PutMapping("/{id}")
    public ApplicationDTO updateById(@Valid @RequestBody UpdateApplicationDTO updateApplicationDTO, @PathVariable Long id) {
        return service.updateById(id, updateApplicationDTO).toDto();
    }

    @PutMapping("/{id}/admin/archive")
    public ApplicationDTO updateIsArchivedById(@RequestBody UpdateApplicationIsArchivedDTO updateApplicationIsArchivedDTO,
                                               @PathVariable Long id) {
        return service.updateIsArchivedById(id, updateApplicationIsArchivedDTO).toDto();
    }
}
