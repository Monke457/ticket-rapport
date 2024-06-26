package ch.kauz.ticketrapport.validation;

import ch.kauz.ticketrapport.services.UserService;
import ch.kauz.ticketrapport.validation.constraints.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Validator for the {@link UniqueEmail} annotation.
 */
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserService service;

    /**
     * Checks if there is already a persisted user entry in the database with the given email - is valid if no such user exists.
     *
     * @param value object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return true if valid, otherwise false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return service.findByEmail(value) == null;
    }
}
