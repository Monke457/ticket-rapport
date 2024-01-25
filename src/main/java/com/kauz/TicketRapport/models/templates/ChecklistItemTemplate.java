package com.kauz.TicketRapport.models.templates;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a checklist item template in the database.
 * Must have an id.
 * May have a description and be a part of any number of checklist templates.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChecklistItemTemplate implements DBEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "Please enter a description of the checklist item")
    private String description;

    @ManyToMany(mappedBy = "items")
    private Set<ChecklistTemplate> templates = new HashSet<>();

    public ChecklistItemTemplate(String description, Set<ChecklistTemplate> templates) {
        this.description = description;
        this.templates = templates;
    }
}
