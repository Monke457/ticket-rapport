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

    @ManyToMany
    @JoinTable(
            name = "checklist_template_items",
            joinColumns = { @JoinColumn(name = "template_id") },
            inverseJoinColumns = { @JoinColumn(name = "item_id") }
    )
    private Set<ChecklistItemTemplate> items = new HashSet<>();

    public ChecklistTemplate(Set<ChecklistItemTemplate> items) {
        this.items = items;
    }
}
