package ch.kauz.ticketrapport.validation;

import ch.kauz.ticketrapport.dtos.TemplateItemDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * Custom validation for template models.
 */
@Component
public class TemplateValidator {

    /**
     * Takes a list of template item data transfer objects and checks that the description lengths are valid.
     * Rejects invalid values, adding and errors to the binding result.
     *
     * @param items list of TemplateItemDTO objects to validate
     * @param result the BindingResult object for storing error data
     */
    public void validateItems(List<TemplateItemDTO> items, BindingResult result) {
        boolean itemErr = false;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getDescription().length() == 0) {
                result.rejectValue("items[" + i + "]", "NotBlank", "Bitte geben Sie eine Beschreibung an");
            }
            if (items.get(i).getDescription().length() > 100) {
                result.rejectValue("items[" + i + "]", "Size", "Die Grösse muss zwischen 0 und 100 liegen");
                itemErr = true;
            }
        }
        if (itemErr) {
            result.rejectValue("items", "Size", "Die Grösse der Elementbeschreibung muss zwischen 0 und 100 liegen");
        }
    }
}
