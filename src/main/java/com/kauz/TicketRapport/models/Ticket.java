package com.kauz.TicketRapport.models;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.UUID;

/**
 * Represents a ticket in the database.
 * Must have an id, title, client and status (open, closed, etc.).
 * May have a description, protocol, solution, work time and assigned user.
 * The protocol represents the steps the learner took to work on the ticket.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ticket implements DBEntity {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String title;
    private String description;
    private String protocol;
    private String solution;
    private String worktime;

    @ManyToOne
    private User assignedUser;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Status status;

    public Ticket(String title, String description, String protocol, String solution, String worktime, User assignedUser, Client client, Status status) {
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
