package com.kauz.TicketRapport.models.mappers;

import com.kauz.TicketRapport.models.dtos.ChecklistItemDTO;
import com.kauz.TicketRapport.models.dtos.ChecklistDTO;

public class ChecklistMapper {

    public ChecklistDTO mapDTO(String[] ids, String[] descriptions, String[] checkboxes) {
        return addItems(new ChecklistDTO(), ids, descriptions, checkboxes);
    }

    public ChecklistDTO mapDTO(boolean save, String name, String[] ids, String[] descriptions, String[] checkboxes) {
        ChecklistDTO result = new ChecklistDTO(save, name);
        return addItems(result, ids, descriptions, checkboxes);
    }

    private ChecklistDTO addItems(ChecklistDTO dto, String[] ids, String[] descriptions, String[] checkboxes) {
        for (int i = 0; i < ids.length; i++) {
            if (descriptions[i].trim().isBlank()) continue;
            ChecklistItemDTO item = new ChecklistItemDTO(ids[i], descriptions[i], checkboxes[i].equals("true"));
            dto.getItems().add(item);
        }
        return dto;
    }
}
