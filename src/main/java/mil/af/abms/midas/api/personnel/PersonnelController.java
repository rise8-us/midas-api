package mil.af.abms.midas.api.personnel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController extends AbstractCRUDController<Personnel, PersonnelDTO, PersonnelService> {

    @Autowired
    public PersonnelController(PersonnelService service) {
        super(service);
    }

}
