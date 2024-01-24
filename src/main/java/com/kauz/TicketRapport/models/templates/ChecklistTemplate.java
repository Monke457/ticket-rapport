package com.kauz.TicketRapport.models.templates;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a checklist template in the database.
 * Must have an id.
 * May have any number of checklist item templates.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChecklistTemplate implements DBEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String description;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "checklist_template_items",
            joinColumns = { @JoinColumn(name = "template_id") },
            inverseJoinColumns = { @JoinColumn(name = "item_id") }
    )
    @OrderBy("description")
    private Set<ChecklistItemTemplate> items = new HashSet<>();

    public ChecklistTemplate(String description, Set<ChecklistItemTemplate> items) {
        this.description = description;
        this.items = items;
    }
}
