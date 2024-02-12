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
    private List<Object> items = new LinkedList<>();

    public ChecklistPojo(boolean save, String name) {
        this.save = save;
        this.name = name;
    }

    public ChecklistPojo(List<Object> items) {
        this.items = items;
    }
}
