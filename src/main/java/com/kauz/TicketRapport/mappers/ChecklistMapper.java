package com.kauz.TicketRapport.mappers;

import com.kauz.TicketRapport.dtos.ChecklistDTO;
import com.kauz.TicketRapport.dtos.ChecklistItemDTO;
import com.kauz.TicketRapport.models.ChecklistItem;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public ChecklistDTO mapDTO(Stream<ChecklistItemTemplate> itemTemplates) {
        ChecklistDTO result = new ChecklistDTO();
        result.setItems(itemTemplates
                .map(i -> new ChecklistItemDTO(i.getDescription()))
                .collect(Collectors.toList())
        );
        return result;
    }

    public ChecklistDTO mapDTO(Collection<ChecklistItem> items) {
        ChecklistDTO result = new ChecklistDTO();
        result.setItems(items.stream()
                .sorted(Comparator.comparing(ChecklistItem::getPosition))
                .map(i -> new ChecklistItemDTO(i.getDescription(), i.isCompleted()))
                .collect(Collectors.toList())
        );
        return result;
    }
}
