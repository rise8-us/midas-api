package mil.af.abms.midas.api.gantt.target;

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
import mil.af.abms.midas.api.gantt.target.dto.CreateTargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.TargetDTO;
import mil.af.abms.midas.api.gantt.target.dto.UpdateTargetDTO;
import mil.af.abms.midas.config.security.annotations.HasTargetCreateAccess;
import mil.af.abms.midas.config.security.annotations.HasTargetDeleteAccess;
import mil.af.abms.midas.config.security.annotations.HasTargetUpdateAccess;

@RestController
@RequestMapping("/api/gantt_targets")
public class TargetController extends AbstractCRUDController<Target, TargetDTO, TargetService> {

    @Autowired
    public TargetController(TargetService service) {
        super(service);
    }

    @HasTargetCreateAccess
    @PostMapping
    public TargetDTO create(@Valid @RequestBody CreateTargetDTO createTargetDTO) {
        return service.create(createTargetDTO).toDto();
    }

    @HasTargetUpdateAccess
    @PutMapping("/{id}")
    public TargetDTO updateById(@Valid @RequestBody UpdateTargetDTO updateTargetDTO, @PathVariable Long id) {
        return service.updateById(id, updateTargetDTO).toDto();
    }

    @Override
    @HasTargetDeleteAccess
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

}
