package ch.kauz.ticketrapport.validation.constraints;

import ch.kauz.ticketrapport.validation.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This constraint requires the given Email to be unique, i.e. only one may exist in the database.
 * <p>
 *     Allows for validation error to be caught pre-insert rather than trying to insert an invalid user and throwing an SQL Exception.
 * </p>
 */
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String message() default "Ein Konto existiert bereits unter diesem Email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
