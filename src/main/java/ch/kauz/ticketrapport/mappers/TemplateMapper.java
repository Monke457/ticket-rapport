package ch.kauz.ticketrapport.mappers;

import ch.kauz.ticketrapport.dtos.TemplateChecklistItemsDTO;
import ch.kauz.ticketrapport.dtos.TemplateItemDTO;
import ch.kauz.ticketrapport.models.templates.TemplateChecklist;
import ch.kauz.ticketrapport.models.templates.TemplateChecklistItems;
import ch.kauz.ticketrapport.models.templates.TemplateItem;
import ch.kauz.ticketrapport.services.TemplateItemService;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

/**
 * A component for mapping template checklist and template item data to their respective data transfer objects.
 */
@Component
public class TemplateMapper {

    @Autowired
    private TemplateItemService service;

    /**
     * Maps template item data from JSON Array to a list of DTOs.
     *
     * @param data the JSON Array data as a string
     * @return a list of {@link TemplateItemDTO} objects
     */
    public List<TemplateItemDTO> mapTemplateItemDTOs(String data) throws JSONException {
        List<TemplateItemDTO> items = new ArrayList<>();

        if (data.isBlank()) {
            return items;
        }

        JSONArray json = new JSONArray(data);
        for (int i = 0; i < json.length(); i++) {
            items.add(TemplateItemDTO.ofJSONObject(json.getJSONObject(i)));
        }
        return items;
    }

    /**
     * Maps a combination of database entries and DTOs (selected items from the multiselect list) to template item DTOs.
     *
     * <p>
     *     if a selected item has a valid UUID, the id is added to an exclude list.
     *     <br>
     *     All entries are fetched from the database except those with the excluded ids and mapped to DTOs.
     * </p>
     * <p>
     *     The collections are combined into a single stream and returned.
     * </p>
     *
     * @param selectedItems the selected item DTOs
     * @return a stream of {@link TemplateItemDTO} objects representing all database entries and newly created items.
     */
    public Stream<TemplateItemDTO> mapAllTemplateItemDTOs(TemplateChecklistItemsDTO selectedItems) {

        List<UUID> selectedIds = selectedItems.getItems().stream()
                .filter(i -> i.getId().length() > 5)
                .map(i -> UUID.fromString(i.getId()))
                .toList();

        Stream<TemplateItemDTO> dbItems = service.getAllExcept(selectedIds)
                .map(TemplateItemDTO::ofEntity);

        return Stream.concat(selectedItems.getItems().stream(), dbItems)
                .sorted(Comparator
                        .comparing(TemplateItemDTO::isSelected).reversed()
                        .thenComparing(TemplateItemDTO::getOrdinal)
                        .thenComparing(TemplateItemDTO::getDescription));
    }

    /**
     * Creates a list of template checklist items - i.e., creates a associations between a template checklist and template items.
     *
     * @param checklist the {@link TemplateChecklist} object to be used in the {@link TemplateChecklistItems} objects
     * @param items {@link TemplateItemDTO} objects to map to {@link TemplateItem} objects
     * @return a list of {@link TemplateChecklistItems} representing the associations between a template checklist and template items
     */
    public List<TemplateChecklistItems> mapChecklistItems(TemplateChecklist checklist, List<TemplateItemDTO> items) {
        List<TemplateChecklistItems> checklistItems = new ArrayList<>();
        for (TemplateItemDTO item : items) {
            TemplateItem i = null;
            if (item.getId().length() > 5) {
                try {
                    i = service.get(TemplateItem.class, UUID.fromString(item.getId()));
                } catch (IllegalArgumentException ignore) {}
            }
            if (i == null) {
                i = new TemplateItem(item.getDescription(), new HashSet<>());
            }
            checklistItems.add(new TemplateChecklistItems(checklist, i, item.getOrdinal()));
        }
        return checklistItems;
    }
}
