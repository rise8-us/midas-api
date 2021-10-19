package mil.af.abms.midas.api.epic;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.dtos.AddGitLabEpicDTO;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.config.security.annotations.HasEpicHideAccess;

@RestController
@RequestMapping("/api/epics")
public class EpicController extends AbstractCRUDController<Epic, EpicDTO, EpicService> {

    @Autowired
    public EpicController(EpicService service) {
        super(service);
    }

    @PostMapping
    public EpicDTO create(@Valid @RequestBody AddGitLabEpicDTO addGitLabEpicDTO) {
        return service.create(addGitLabEpicDTO).toDto();
    }

    @GetMapping("/sync/{id}")
    public EpicDTO syncById(@PathVariable Long id) {
        return service.updateById(id).toDto();
    }

    @GetMapping("/all/{productId}")
    public List<EpicDTO> getAllGroupEpics(@PathVariable Long productId) {
        return service.getAllGitlabEpicsForProduct(productId).stream().map(Epic::toDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}/hide")
    @HasEpicHideAccess
    public EpicDTO updateIsHidden(@Valid @RequestBody IsHiddenDTO isHiddenDTO, @PathVariable Long id) {
        return service.updateIsHidden(id, isHiddenDTO).toDto();
    }

}
