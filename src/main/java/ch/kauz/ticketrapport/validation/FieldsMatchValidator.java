package ch.kauz.ticketrapport.validation;

import ch.kauz.ticketrapport.validation.constraints.FieldsMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Validator for the {@link FieldsMatch} annotation.
 */
public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {

    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        field = constraintAnnotation.field();
        fieldMatch = constraintAnnotation.fieldMatch();
    }

    /**
     * Compares values of both marked fields on an object - is valid if they are the same.
     *
     * @param value object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return true if valid, otherwise false
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
        Object matchValue = new BeanWrapperImpl(value).getPropertyValue(fieldMatch);

        if (fieldValue == null) {
            return matchValue == null;
        }
        return fieldValue.equals(matchValue);
    }
}
