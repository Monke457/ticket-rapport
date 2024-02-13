package com.kauz.TicketRapport.mappers;

import com.kauz.TicketRapport.dtos.ChecklistItemDTO;
import com.kauz.TicketRapport.dtos.ChecklistDTO;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class ChecklistMapper {

    public ChecklistDTO mapDTO(boolean save, String name, String[] ids, String[] descriptions, String[] checkboxes) {
        List<ChecklistItemDTO> items = new LinkedList<>();
        for (int i = 0; i < ids.length; i++) {
            if (descriptions[i].trim().isBlank()) continue;
            items.add(new ChecklistItemDTO(ids[i], descriptions[i], checkboxes[i].equals("true")));
        }
        return new ChecklistDTO(save, name, items);
    }
}
