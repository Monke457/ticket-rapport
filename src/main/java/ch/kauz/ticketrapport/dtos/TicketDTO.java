package ch.kauz.ticketrapport.dtos;

import ch.kauz.ticketrapport.models.Status;
import ch.kauz.ticketrapport.models.Ticket;
import ch.kauz.ticketrapport.models.User;
import ch.kauz.ticketrapport.models.helpers.StatusType;
import jakarta.persistence.OrderBy;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing a ticket.
 * <p>
 *     Contains information pertaining to the ticket entity.
 *     Relational data are converted to strings.
 *     <br>
 *     Also includes styling information.
 * </p>
 */
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private UUID id;
    private String title;
    private String description;
    private String protocol;
    private String solution;
    private String workTime;
    private UUID learnerId;
    private String learner;
    private UUID clientId;
    private String client;
    private String status;
    private String styleClass;

    @OrderBy("ordinal")
    private List<ChecklistItemDTO> checklistItems;

    public static TicketDTO ofEntity(Ticket entity) {
        return TicketDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .protocol(entity.getProtocol())
                .solution(entity.getSolution())
                .workTime(getWorkTime(entity.getWorkHours(), entity.getWorkMinutes()))
                .learnerId(entity.getLearner() == null ? null : entity.getLearner().getId())
                .learner(entity.getLearner() == null ? "Unzugewiesen" : entity.getLearner().getFullName())
                .clientId(entity.getClient() == null ? null : entity.getClient().getId())
                .client(entity.getClient() == null ? "Unzugewiesen" : entity.getClient().getName())
                .status(entity.getStatus() == null ? "Keiner" : entity.getStatus().getDescription())
                .checklistItems(entity.getChecklistItems().stream().map(ChecklistItemDTO::ofEntity).toList())
                .styleClass(createStyle(entity.getStatus(), entity.getLearner()))
                .build();
    }

    /**
     * Converts work hours and work minutes into a readable string.
     *
     * @param hours the work hours
     * @param minutes the work minutes
     * @return a string representation of the total work time
     */
    private static String getWorkTime(int hours, int minutes) {
        if (hours == 0 && minutes == 0) {
            return "";
        }
        return (hours > 0 ? hours + "h " : "") + minutes + "m";
    }

    /**
     * Builds style classes depending on ticket status.
     * <p>
     *     Red: unassigned and not closed<br>
     *     Orange: assigned and marked as complete<br>
     *     Blue: assigned and open<br>
     *     Green: closed
     * </p>
     * @param status the status of the ticket
     * @param learner the assigned learner
     * @return a string representation of the classes to apply to a ticket card
     */
    private static String createStyle(Status status, User learner) {
        if (status == null) {
            return "";
        }

        String s = status.getDescription();
        String result = "t-left-border ";

        if (learner == null) {
            result +=  "t-red";
            return result;
        }
        if (s.equals(StatusType.CLOSED.getDisplay())) {
            result += "t-green";
        } else if (s.equals(StatusType.COMPLETED.getDisplay())) {
            result +=  "t-orange";
        } else if (s.equals(StatusType.OPEN.getDisplay())) {
            result += "t-blue";
        }
        return result;
    }

    /* This method is called from a template, do not remove */
    public boolean isCompleted() {
        return StatusType.COMPLETED.getDisplay().equals(status);
    }

    /* This method is called from a template, do not remove */
    public boolean isOpen() {
        return StatusType.OPEN.getDisplay().equals(status);
    }
}
