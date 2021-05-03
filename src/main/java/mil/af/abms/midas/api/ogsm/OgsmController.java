package mil.af.abms.midas.api.ogsm;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.ogsm.dto.CreateOgsmDTO;
import mil.af.abms.midas.api.ogsm.dto.OgsmDTO;

@RestController
@RequestMapping("/api/ogsms")
public class OgsmController extends AbstractCRUDController<Ogsm, OgsmDTO, OgsmService> {

    @Autowired
    public OgsmController(OgsmService service) { super(service); }

    @PostMapping
    public OgsmDTO create(@Valid @RequestBody CreateOgsmDTO createOgsmDTO) {
        return service.create(createOgsmDTO).toDto();
    }

}
