package mil.af.abms.midas.api.release;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.release.dto.CreateReleaseDTO;
import mil.af.abms.midas.api.release.dto.ReleaseDTO;
import mil.af.abms.midas.api.release.dto.UpdateReleaseDTO;

@RestController
@RequestMapping("/api/releases")
public class ReleaseController extends AbstractCRUDController<Release, ReleaseDTO, ReleaseService> {

    public ReleaseController(ReleaseService service) { super(service); }

    @PostMapping
    public ReleaseDTO create(@Valid @RequestBody CreateReleaseDTO createReleaseDTO) {
        return service.create(createReleaseDTO).toDto();
    }

    @PutMapping("/{id}")
    public ReleaseDTO updateById(@Valid @RequestBody UpdateReleaseDTO updateReleaseDTO, @PathVariable Long id) {
        return service.updateById(id, updateReleaseDTO).toDto();
    }

    @PutMapping("/{id}/archive")
    public ReleaseDTO updateIsArchived(@Valid @RequestBody IsArchivedDTO isArchivedDTO, @PathVariable Long id) {
        return service.updateIsArchived(id, isArchivedDTO).toDto();
    }

}
