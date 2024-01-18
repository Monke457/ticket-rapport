package com.kauz.TicketRapport.models;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ticket implements DBEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    private String description;
    private String protocol;
    private String solution;
    private Time worktime;

    @ManyToOne
    private User assignedUser;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Status status;

    public Ticket(String title, String description, String protocol, String solution, Time worktime, User assignedUser, Client client, Status status) {
        this.title = title;
        this.description = description;
        this.protocol = protocol;
        this.solution = solution;
        this.worktime = worktime;
        this.assignedUser = assignedUser;
        this.client = client;
        this.status = status;
    }
}
