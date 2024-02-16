package com.kauz.TicketRapport.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItemDTO {
    private String id;
    private String description;
    private boolean completed;
    private boolean valid = true;
    private String error;

    public ChecklistItemDTO(String id, String description, boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;
    }

    public ChecklistItemDTO(String description) {
        this.description = description;
    }

    public ChecklistItemDTO(String description, boolean completed) {
        this.description = description;
        this.completed = completed;
    }
}
