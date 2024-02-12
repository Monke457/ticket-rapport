package com.kauz.TicketRapport.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a status in the database.
 * Must have an id and a description.
 * May be assigned to any number of Tickets.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Status implements DBEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String description;

    public Status(String description) {
        this.description = description;
    }
}
