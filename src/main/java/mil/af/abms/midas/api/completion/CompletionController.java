package mil.af.abms.midas.api.completion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.completion.dto.CompletionDTO;

@RestController
@RequestMapping("/api/completions")
public class CompletionController extends AbstractCRUDController<Completion, CompletionDTO, CompletionService> {

    @Autowired
    public CompletionController(CompletionService service) { super(service); }

}
