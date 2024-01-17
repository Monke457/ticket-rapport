package com.kauz.TicketRapport.model;

import com.kauz.TicketRapport.model.helpers.DBEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChecklistItem implements DBEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String description;
    private boolean completed;

    @ManyToOne
    private Ticket ticket;

    public ChecklistItem(String description, boolean completed, Ticket ticket) {
        this.description = description;
        this.completed = completed;
        this.ticket = ticket;
    }
}
