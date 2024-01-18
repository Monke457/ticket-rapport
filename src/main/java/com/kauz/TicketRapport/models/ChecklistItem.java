package com.kauz.TicketRapport.models;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

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
    private String description;

    @Column(nullable = false)
    private boolean completed;

    @ManyToOne
    @Column(nullable = false)
    private Ticket ticket;

    public ChecklistItem(String description, boolean completed, Ticket ticket) {
        this.description = description;
        this.completed = completed;
        this.ticket = ticket;
    }
}
