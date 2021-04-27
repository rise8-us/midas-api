package mil.af.abms.midas.api.problem;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.problem.dto.CreateProblemDTO;
import mil.af.abms.midas.api.problem.dto.ProblemDTO;
import mil.af.abms.midas.api.problem.dto.UpdateProblemDTO;
import mil.af.abms.midas.api.problem.dto.UpdateProblemIsCurrentDTO;
import mil.af.abms.midas.config.security.annotations.IsAdmin;

@RestController
@RequestMapping("/api/problems")
public class ProblemController extends AbstractCRUDController<Problem, ProblemDTO, ProblemService> {

    @Autowired
    public ProblemController(ProblemService service) {
        super(service);
    }

    @PostMapping
    public ProblemDTO create(@Valid @RequestBody CreateProblemDTO createProblemDTO) {
        return service.create(createProblemDTO).toDto();
    }

    @PutMapping("/{id}")
    public ProblemDTO updateById(@Valid @RequestBody UpdateProblemDTO updateProblemDTO, @PathVariable Long id) {
        return service.updateById(id, updateProblemDTO).toDto();
    }

    @IsAdmin
    @PutMapping("/{id}/current")
    public ProblemDTO updateIsCurrentById(@RequestBody UpdateProblemIsCurrentDTO updateProblemIsCurrentDTO,
                                           @PathVariable Long id) {
        return service.updateIsCurrentById(id, updateProblemIsCurrentDTO).toDto();
    }

}
