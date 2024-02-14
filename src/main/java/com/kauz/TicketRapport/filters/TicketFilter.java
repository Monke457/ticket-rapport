package com.kauz.TicketRapport.filters;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * A filter class specifically for the ticket entity.
 * Inherits all the general filter, sorting and pagination attributes from the parent filter class.
 * may also have a learner id, client id, status id and a status,
 * represented by a comma separated string of any number of status descriptions.
 */
@Getter
@Setter
@NoArgsConstructor
public class TicketFilter extends Filter {
    private UUID learnerId;
    private UUID clientId;
    private UUID statusId;

    // multiple statuses separated by commas (this can just be an array)
    private String status;

    public TicketFilter(String search, String sort, int page, boolean asc, UUID clientId, UUID statusId) {
        super(search, sort, page, asc);
        this.clientId = clientId;
        this.statusId = statusId;
    }

    public TicketFilter(String search, UUID clientId, String status) {
        super(search);
        this.clientId = clientId;
        this.status = status;
    }

    @Override
    public String toQueryString(Integer newPage) {
        return super.toQueryString(newPage)
                + "&clientId=" + (clientId == null ? "" : clientId)
                + "&statusId=" + (statusId == null ? "" : statusId);
    }

    @Override
    public String toQueryString(String newSort) {
        return super.toQueryString(newSort)
                + "&clientId=" + (clientId == null ? "" : clientId)
                + "&statusId=" + (statusId == null ? "" : statusId);
    }
}
