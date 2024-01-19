package com.kauz.TicketRapport.models.helpers;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Status;
import com.kauz.TicketRapport.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketFormData {
    private UUID id;
    private String title;
    private String description;
    private String protocol;
    private String solution;
    private int hours;
    private int minutes;
    private User assignedUser;
    private Client client;
    private Status status;
}
