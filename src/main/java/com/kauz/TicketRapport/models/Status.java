package com.kauz.TicketRapport.models;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

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
