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
    @NotBlank(message = "Please enter a description of the role")
    private String description;

    public Role(String description) {
        this.description = description;
    }
}
