package com.kauz.TicketRapport.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a role in the database.
 * Must have an id and a description.
 * May be linked to any number of users.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role implements DBEntity {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String description;

    public Role(String description) {
        this.description = description;
    }
}
