package com.kauz.TicketRapport.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistDTO {
    private boolean save;
    private String name;
    private List<ChecklistItemDTO> items = new LinkedList<>();
    private boolean valid = true;
    private String error;

    public ChecklistDTO(boolean save, String name) {
        this.save = save;
        this.name = name;
    }

    public boolean hasErrors() {
        if (!valid) return true;
        for (ChecklistItemDTO item : items) {
            if (!item.isValid()) return true;
        }
        return false;
    }
}
