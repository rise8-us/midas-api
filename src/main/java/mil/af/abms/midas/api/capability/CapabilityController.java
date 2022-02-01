package mil.af.abms.midas.api.capability;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.capability.dto.CapabilityDTO;
import mil.af.abms.midas.api.capability.dto.CreateCapabilityDTO;
import mil.af.abms.midas.api.capability.dto.UpdateCapabilityDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.config.security.annotations.HasPortfolioAdminOrAdmin;

@RestController
@RequestMapping("/api/capabilities")
public class CapabilityController extends AbstractCRUDController<Capability, CapabilityDTO, CapabilityService> {

    public CapabilityController(CapabilityService service) { super(service); }

    @HasPortfolioAdminOrAdmin
    @PostMapping
    public CapabilityDTO create(@Valid @RequestBody CreateCapabilityDTO createCapabilityDTO) {
        return service.create(createCapabilityDTO).toDto();
    }

    @HasPortfolioAdminOrAdmin
    @PutMapping("/{id}")
    public CapabilityDTO updateById(@Valid @RequestBody UpdateCapabilityDTO updateCapabilityDTO, @PathVariable Long id) {
        return service.updateById(id, updateCapabilityDTO).toDto();
    }

    @HasPortfolioAdminOrAdmin
    @PutMapping("/{id}/archive")
    public CapabilityDTO updateIsArchived(@Valid @RequestBody IsArchivedDTO isArchivedDTO, @PathVariable Long id) {
        return service.updateIsArchived(id, isArchivedDTO).toDto();
    }

    @HasPortfolioAdminOrAdmin
    @DeleteMapping("/{id}")
    @Override
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }
}
