package com.kauz.TicketRapport.models.filters;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TicketFilter extends Filter {
    private UUID learnerId;
    private UUID clientId;

    // multiple statuses separated by commas
    private String status;

    public TicketFilter(String search, String sort, int page, boolean asc, UUID clientId, String status) {
        super(search, sort, page, asc);
        this.clientId = clientId;
        this.status = status;
    }

    public TicketFilter(String search, UUID clientId, String status) {
        super(search);
        this.clientId = clientId;
        this.status = status;
    }
}
