package com.kauz.TicketRapport.models;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a ticket in the database.
 * Must have an id, title, client and status (open, closed, etc.).
 * May have a description, protocol, solution, work time and assigned user.
 * The protocol represents the steps the learner took to work on the ticket.
 * May also have any number of checklist items.
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
    private int workHours;
    private int workMinutes;

    @ManyToOne
    private User assignedUser;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Status status;

    @OneToMany(mappedBy = "ticket")
    private Set<ChecklistItem> checklist = new HashSet<>();

    @Transient
    private String worktime;

    public Ticket(String title, String description, String protocol, String solution, int workHours, int workMinutes, User assignedUser, Client client, Status status, Set<ChecklistItem> checklist) {
        this.title = title;
        this.description = description;
        this.protocol = protocol;
        this.solution = solution;
        this.workHours = workHours;
        this.workMinutes = workMinutes;
        this.assignedUser = assignedUser;
        this.client = client;
        this.status = status;
        this.checklist = checklist;
    }

    public String getWorktime() {
        return workHours + "h " + workMinutes + "m";
    }
}
