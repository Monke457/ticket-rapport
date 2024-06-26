package ch.kauz.ticketrapport.validation;

import ch.kauz.ticketrapport.models.Role;
import ch.kauz.ticketrapport.validation.constraints.ValidRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the {@link ValidRole} annotation.
 */
public class RoleValidator implements ConstraintValidator<ValidRole, Role> {

    /**
     * Checks if the role has an id - is valid if it does.
     *
     * @param value role to validate
     * @param context context in which the constraint is evaluated
     *
     * @return true if valid, otherwise false
     */
    @Override
    public boolean isValid(Role value, ConstraintValidatorContext context) {
        return value.getId() != null;
    }
}
