package ch.kauz.ticketrapport.validation.constraints;

import ch.kauz.ticketrapport.validation.FieldsMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This constraint requires two fields to have the same value.
 * <p>
 *     The names of the fields are denoted by the field and fieldMatch attributes.
 * </p>
 */
@Constraint(validatedBy = FieldsMatchValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsMatch {
    String field();
    String fieldMatch();
    String message() default "Die Felder sind ungleich";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
