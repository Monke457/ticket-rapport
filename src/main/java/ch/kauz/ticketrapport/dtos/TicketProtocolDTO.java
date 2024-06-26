package ch.kauz.ticketrapport.dtos;

import ch.kauz.ticketrapport.models.Ticket;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * Data Transfer Object representing the protocol fields of a ticket.
 * <p>
 *     Contains information pertaining only to the fields learners are permitted to update:
 *     <br>
 *     protocol, solution, workHours, workMinutes, and checklistItems
 * </p>
 * <p>
 *     For isolating the fields to update and validate when a learner protocols their ticket.
 * </p>
 */
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TicketProtocolDTO {
    @Size(max = 2000, message = "Die Grösse muss zwischen 0 und 2000 liegen")
    private String protocol;
    @Size(max = 2000, message = "Die Grösse muss zwischen 0 und 2000 liegen")
    private String solution;

    private int workHours;
    private int workMinutes;

    @OrderBy("ordinal")
    private List<ChecklistItemDTO> checklistItems;

    public static TicketProtocolDTO ofEntity(Ticket entity) {
        return TicketProtocolDTO.builder()
                .protocol(entity.getProtocol())
                .solution(entity.getSolution())
                .workHours(entity.getWorkHours())
                .workMinutes(entity.getWorkMinutes())
                .checklistItems(entity.getChecklistItems().stream().map(ChecklistItemDTO::ofEntity).toList())
                .build();
    }
}
