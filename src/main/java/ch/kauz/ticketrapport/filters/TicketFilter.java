package ch.kauz.ticketrapport.filters;

import ch.kauz.ticketrapport.models.Ticket;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * A ticket specific filter that inherits all the general filter fields.
 * <p>
 *     May also include a client id, status id and status string.
 * </p>
 * @see Filter
 * @see Ticket
 */
@Getter
@Setter
public class TicketFilter extends Filter {
    private UUID clientId;
    private UUID statusId;
    private String status;

    @Override
    public String toQueryString(int page) {
        return super.toQueryString(page)
                + (clientId == null ? "" : "&clientId=" + clientId)
                + (statusId == null ? "" : "&statusId=" + statusId);

    }

    @Override
    public String toQueryString(String sort) {
        return super.toQueryString(sort)
                + (clientId == null ? "" : "&clientId=" + clientId)
                + (statusId == null ? "" : "&statusId=" + statusId);
    }
}
