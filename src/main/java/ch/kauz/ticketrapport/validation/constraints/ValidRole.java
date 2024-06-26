package ch.kauz.ticketrapport.validation.constraints;

import ch.kauz.ticketrapport.validation.RoleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This constraint requires a Role to be valid: corresponds to an existing database entry.
 * <p>
 *     Avoids the issue when selecting an empty role option and the app logic automatically generating a new Role object with no id.
 * </p>
 */
@Constraint(validatedBy = RoleValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRole {
    String message() default "Bitte wählen Sie eine Rolle aus";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
