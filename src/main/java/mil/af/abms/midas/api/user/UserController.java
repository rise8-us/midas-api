package mil.af.abms.midas.api.user;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.user.dto.UpdateUserDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserDisabledDTO;
import mil.af.abms.midas.api.user.dto.UpdateUserRolesDTO;
import mil.af.abms.midas.api.user.dto.UserDTO;
import mil.af.abms.midas.config.security.annotations.HasUserUpdateAccess;
import mil.af.abms.midas.config.security.annotations.IsAdmin;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController extends AbstractCRUDController<User, UserDTO, UserService> {

    @Autowired
    public UserController(UserService service) {
        super(service);
    }

    @HasUserUpdateAccess
    @PutMapping("/{id}")
    public UserDTO updateById(@Valid @RequestBody UpdateUserDTO updateUserDTO, @PathVariable Long id) {
        return service.updateById(id, updateUserDTO).toDto();
    }

    @IsAdmin
    @PutMapping("/{id}/roles")
    public UserDTO updateRolesById(@RequestBody UpdateUserRolesDTO updateUserRolesDTO, @PathVariable Long id) {
        return service.updateRolesById(id, updateUserRolesDTO).toDto();
    }

    @IsAdmin
    @PutMapping("/{id}/disable")
    public UserDTO updateIsDisabledById(@RequestBody UpdateUserDisabledDTO updateUserDisabledDTO,
                                        @PathVariable Long id) {
        return service.updateIsDisabledById(id, updateUserDisabledDTO).toDto();
    }

}
