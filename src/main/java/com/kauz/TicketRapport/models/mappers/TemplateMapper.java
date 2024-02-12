package com.kauz.TicketRapport.models.mappers;

import com.kauz.TicketRapport.models.pojos.ItemTemplatePojo;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;

import java.util.*;
import java.util.stream.Collectors;

public class TemplateMapper {
    public List<ItemTemplatePojo> mapPojos(Collection<ChecklistItemTemplate> items) {
        return mapPojos(items, new HashSet<>());
    }

    public List<ItemTemplatePojo> mapPojos(Collection<ChecklistItemTemplate> items, Collection<ChecklistItemTemplate> checkedItems) {
        List<ItemTemplatePojo> result = addPojos(items, checkedItems);

        return sortPojos(result);
    }

    public List<ItemTemplatePojo> mapPojos(Collection<ChecklistItemTemplate> items, String checkedItems) {
        List<ItemTemplatePojo> result = addPojos(items, new HashSet<>());

        for (ItemTemplatePojo item : result) {
            if (checkedItems.contains(item.getDescription())) {
                item.setChecked(true);
                checkedItems = checkedItems.replace(item.getDescription(), "");
            }
        }

        List<ItemTemplatePojo> newItems = Arrays.stream(checkedItems.split(","))
                .filter(s -> !s.isBlank())
                .map(s -> new ItemTemplatePojo(null, s, true))
                .toList();
        result.addAll(newItems);

        return sortPojos(result);
    }

    private List<ItemTemplatePojo> addPojos(Collection<ChecklistItemTemplate> items, Collection<ChecklistItemTemplate> checkedItems) {
        return items.stream()
                .map(i -> new ItemTemplatePojo(
                        i.getId(),
                        i.getDescription(),
                        checkedItems.contains(i)))
                .collect(Collectors.toList());
    }

    private List<ItemTemplatePojo> sortPojos(Collection<ItemTemplatePojo> pojos) {
        return pojos.stream().sorted(Comparator
                        .comparing(ItemTemplatePojo::isChecked)
                        .reversed()
                        .thenComparing(ItemTemplatePojo::getDescription))
                .collect(Collectors.toList());
    }
}
