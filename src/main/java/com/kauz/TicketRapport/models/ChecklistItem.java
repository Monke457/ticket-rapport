package com.kauz.TicketRapport.models;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a checklist item in the database.
 * Must have an id, a completed status, and a Ticket.
 * May have a description.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChecklistItem implements DBEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "Please enter a description of the checklist item")
    private String description;

    @Column(nullable = false)
    private boolean completed;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Ticket ticket;

    public ChecklistItem(String description, boolean completed, Ticket ticket) {
        this.description = description;
        this.completed = completed;
        this.ticket = ticket;
    }
}
