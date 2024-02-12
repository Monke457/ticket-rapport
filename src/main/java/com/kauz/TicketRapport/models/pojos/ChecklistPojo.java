package com.kauz.TicketRapport.models.pojos;

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
public class ChecklistPojo {
    private boolean save;
    private String name;
    private List<ChecklistItemPojo> items = new LinkedList<>();
    private boolean valid = true;
    private String error;

    public ChecklistPojo(boolean save, String name) {
        this.save = save;
        this.name = name;
    }

    public boolean hasErrors() {
        if (!valid) return true;
        for (ChecklistItemPojo item : items) {
            if (!item.isValid()) return true;
        }
        return false;
    }
}
