package mil.af.abms.midas.api.sourcecontrol;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.sourcecontrol.dto.CreateUpdateSourceControlDTO;
import mil.af.abms.midas.api.sourcecontrol.dto.SourceControlDTO;

@RestController
@RequestMapping("/api/sourceControls")
public class SourceControlController extends AbstractCRUDController<SourceControl, SourceControlDTO, SourceControlService> {

    @Autowired
    public SourceControlController(SourceControlService service) {
        super(service);
    }

    @PostMapping
    public SourceControlDTO create(@Valid @RequestBody CreateUpdateSourceControlDTO cDto) {
        return service.create(cDto).toDto();
    }

    @PutMapping("/{id}")
    public SourceControlDTO create(@PathVariable Long id, @Valid @RequestBody CreateUpdateSourceControlDTO uDto) {
        return service.updateById(id, uDto).toDto();
    }

}
