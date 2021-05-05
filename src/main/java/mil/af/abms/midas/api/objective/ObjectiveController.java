package mil.af.abms.midas.api.objective;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.objective.dto.CreateObjectiveDTO;
import mil.af.abms.midas.api.objective.dto.ObjectiveDTO;

@RestController
@RequestMapping("/api/objectives")
public class ObjectiveController extends AbstractCRUDController<Objective, ObjectiveDTO, ObjectiveService> {

    @Autowired
    public ObjectiveController(ObjectiveService service) { super(service); }

    @PostMapping
    public ObjectiveDTO create(@Valid @RequestBody CreateObjectiveDTO createObjectiveDTO) {
        return service.create(createObjectiveDTO).toDto();
    }

}
