package com.kauz.TicketRapport.models;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a client in the database.
 * Must have an id and a name.
 * May be assigned to any number of Tickets.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client implements DBEntity {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    @NotBlank(message = "Please enter a name")
    private String name;

    public Client(String name) {
        this.name = name;
    }
}
