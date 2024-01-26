package com.kauz.TicketRapport.models;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a ticket in the database.
 * Must have an id, title, client.
 * May have a description, protocol, solution, work time, assigned user, and status (open, closed, etc.).
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
    @NotBlank(message = "Please enter a title")
    private String title;
    private String description;
    private String protocol;
    private String solution;
    private int workHours;
    private int workMinutes;

    @ManyToOne
    private User assignedUser;

    @ManyToOne
    private Client client;


    @ManyToOne
    private Status status;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
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

    /**
     * A method for getting the styling info for a ticket card.
     *
     * @return a string of style attributes.
     */
    public String getCardStyle() {
        // no status no style
        if (status.getDescription().isBlank()) return "";
        return "border-left: 15px solid " + getBackgroundColor() + ";";
    }

    /**
     * Returns a css colour code based on status.
     * <br>In progress unassigned: red
     * <br>In Progress assigned: light blue
     * <br>Completed: orange
     * <br>Closed: light green
     *
     * @return a colour code for css styling.
     */
    private String getBackgroundColor() {
        if (status.getDescription().equals("In Progress")) {
            return assignedUser == null ? "#dc3545" : "lightblue";
        }
        if (status.getDescription().equals("Completed")) return "#ed6c00";
        if (status.getDescription().equals("Closed")) return "#ade3ad";
        // no status no colour
        return "";
    }
}
