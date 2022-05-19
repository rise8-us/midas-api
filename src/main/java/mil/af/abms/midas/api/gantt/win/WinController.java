package mil.af.abms.midas.api.gantt.win;

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
import mil.af.abms.midas.api.gantt.win.dto.CreateWinDTO;
import mil.af.abms.midas.api.gantt.win.dto.UpdateWinDTO;
import mil.af.abms.midas.api.gantt.win.dto.WinDTO;
import mil.af.abms.midas.config.security.annotations.HasWinCreateAccess;
import mil.af.abms.midas.config.security.annotations.HasWinModifyAccess;

@RestController
@RequestMapping("/api/gantt_wins")
public class WinController extends AbstractCRUDController<Win, WinDTO, WinService> {

    @Autowired
    public WinController(WinService service) {
        super(service);
    }

    @HasWinCreateAccess
    @PostMapping
    public WinDTO create(@Valid @RequestBody CreateWinDTO createWinDTO) {
        return service.create(createWinDTO).toDto();
    }

    @HasWinModifyAccess
    @PutMapping("/{id}")
    public WinDTO updateById(@Valid @RequestBody UpdateWinDTO updateWinDTO, @PathVariable Long id) {
        return service.updateById(id, updateWinDTO).toDto();
    }

    @Override
    @HasWinModifyAccess
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

}
