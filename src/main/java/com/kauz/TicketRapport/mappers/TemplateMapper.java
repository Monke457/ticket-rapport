package com.kauz.TicketRapport.mappers;

import com.kauz.TicketRapport.dtos.ItemTemplateDTO;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;

import java.util.*;
import java.util.stream.Collectors;

public class TemplateMapper {
    public List<ItemTemplateDTO> mapDTO(Collection<ChecklistItemTemplate> items) {
        return mapDTO(items, new HashSet<>());
    }

    public List<ItemTemplateDTO> mapDTO(Collection<ChecklistItemTemplate> items, Collection<ChecklistItemTemplate> checkedItems) {
        List<ItemTemplateDTO> result = addDTOs(items, checkedItems);

        return sortDTOs(result);
    }

    public List<ItemTemplateDTO> mapDTO(Collection<ChecklistItemTemplate> items, String checkedItems) {
        List<ItemTemplateDTO> result = addDTOs(items, new HashSet<>());

        for (ItemTemplateDTO item : result) {
            if (checkedItems.contains(item.getDescription())) {
                item.setChecked(true);
                checkedItems = checkedItems.replace(item.getDescription(), "");
            }
        }

        List<ItemTemplateDTO> newItems = Arrays.stream(checkedItems.split(","))
                .filter(s -> !s.isBlank())
                .map(s -> new ItemTemplateDTO(null, s, true))
                .toList();
        result.addAll(newItems);

        return sortDTOs(result);
    }

    private List<ItemTemplateDTO> addDTOs(Collection<ChecklistItemTemplate> items, Collection<ChecklistItemTemplate> checkedItems) {
        return items.stream()
                .map(i -> new ItemTemplateDTO(
                        i.getId(),
                        i.getDescription(),
                        checkedItems.contains(i)))
                .collect(Collectors.toList());
    }

    private List<ItemTemplateDTO> sortDTOs(Collection<ItemTemplateDTO> dtos) {
        return dtos.stream().sorted(Comparator
                        .comparing(ItemTemplateDTO::isChecked)
                        .reversed()
                        .thenComparing(ItemTemplateDTO::getDescription))
                .collect(Collectors.toList());
    }
}
