package ch.kauz.ticketrapport.validation;

import ch.kauz.ticketrapport.dtos.ChecklistDTO;
import ch.kauz.ticketrapport.dtos.TicketProtocolDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

/**
 * Custom validation for ticket models.
 */
@Component
public class TicketValidator {

    /**
     * Validates a checklist data transfer object and rejects any invalid fields, adding errors to the binding result.
     *
     * @param checklist the ChecklistDTO to validate
     * @param result the BindingResult object for storing error data
     */
    public void validateChecklist(ChecklistDTO checklist, BindingResult result) {
        if (checklist.isSaveAsTemplate() && checklist.getTemplateName().isBlank()) {
            result.rejectValue("templateName", "NotBlank", "Bitte geben Sie einen Titel an");
        }
        for (int i = 0; i < checklist.getItems().size(); i++) {
            if (checklist.getItems().get(i).getDescription().length() > 100) {
                result.rejectValue("items[" + i + "]", "Size", "Die Grösse muss zwischen 0 und 100 liegen");
            }
        }
    }

    /**
     * Validates a ticket protocol data transfer object and rejects any invalid fields, adding errors to the binding result.
     * <p>
     *     This should only be called if a learner has marked a ticket as complete.
     *     Intermediate save state validation constraints are less strict.
     * </p>
     *
     * @param protocol the TicketProtocolDTO to validate
     * @param result the BindingResult object for storing error data
     */
    public void validateProtocol(TicketProtocolDTO protocol, BindingResult result) {
        if (protocol.getProtocol().isBlank()) {
            result.rejectValue("protocol", "NotBlank", "Bitte geben Sie ein Protokoll an");
        }
        if (protocol.getSolution().isBlank()) {
            result.rejectValue("solution", "NotBlank", "Bitte geben Sie Ihre Lösung an");
        }
        if (protocol.getWorkHours() == 0 && protocol.getWorkMinutes() == 0) {
            result.rejectValue("workMinutes", "Size", "Bitte geben Sie Ihre Arbeitszeit an");
        }
    }
}
