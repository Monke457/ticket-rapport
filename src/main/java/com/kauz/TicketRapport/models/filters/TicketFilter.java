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

    // multiple statuses separated by a comma
    private String status;

    public TicketFilter(String search, UUID learnerId, UUID clientId, String status) {
        super(search);
        this.learnerId = learnerId;
        this.clientId = clientId;
        this.status = status;
    }
}
