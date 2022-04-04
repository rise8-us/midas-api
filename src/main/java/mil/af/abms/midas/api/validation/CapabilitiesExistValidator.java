package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.capability.CapabilityService;

public class CapabilitiesExistValidator implements ConstraintValidator<CapabilitiesExist, Set<Long>> {
    @Autowired
    private CapabilityService capabilityService;

    @Override
    public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
        constraintContext.disableDefaultConstraintViolation();

        if (ids == null) { return true; }

        List<ConstraintValidatorContext> violations = ids.stream()
                .filter(i -> !capabilityService.existsById(i)).map(i -> constraintContext
                        .buildConstraintViolationWithTemplate(String.format("Capability with id: %s does not exists", i))
                        .addConstraintViolation()
                ).collect(Collectors.toList());

        return violations.isEmpty();
    }

}
