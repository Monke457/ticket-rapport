package ch.kauz.ticketrapport.mappers;

import ch.kauz.ticketrapport.dtos.ChecklistItemDTO;
import ch.kauz.ticketrapport.dtos.TicketProtocolDTO;
import ch.kauz.ticketrapport.models.ChecklistItem;
import ch.kauz.ticketrapport.models.Ticket;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * A component for mapping ticket data to data transfer objects.
 */
@Component
public class TicketMapper {

    /**
     * Creates a list of new checklist items from a list of DTOs.
     *
     * @param ticket the {@link Ticket} entry to associate the checklist items with
     * @param items the list of {@link ChecklistItemDTO} object containing the values to map
     * @return a set of {@link ChecklistItem} objects
     */
    public Set<ChecklistItem> mapChecklistItems(Ticket ticket, List<ChecklistItemDTO> items) {
        Set<ChecklistItem> result = new HashSet<>();
        for (ChecklistItemDTO item : items) {
            result.add(new ChecklistItem(item.getId(), item.getOrdinal(), item.getDescription(), item.isCompleted(), ticket));
        }
        return result;
    }

    /**
     * Maps checklist item data from JSON Array to a list of DTOs.
     *
     * @param data the JSON Array data as a string
     * @return a list of {@link ChecklistItemDTO} objects
     */
    public List<ChecklistItemDTO> mapChecklistItemDTOs(String data) throws JSONException {
        List<ChecklistItemDTO> items = new ArrayList<>();

        if (data.isBlank()) {
            return items;
        }

        JSONArray json = new JSONArray(data);
        for (int i = 0; i < json.length(); i++) {
            items.add(ChecklistItemDTO.ofJSONObject(json.getJSONObject(i)));
        }
        return items;
    }

    /**
     * Maps the protocol fields from a dto to a ticket.
     *
     * @param ticket the {@link Ticket} object to map the values to
     * @param protocol the {@link TicketProtocolDTO} object containing the values to map
     */
    public void mapProtocol(Ticket ticket, TicketProtocolDTO protocol) {
        ticket.setProtocol(protocol.getProtocol());
        ticket.setSolution(protocol.getSolution());
        ticket.setWorkHours(protocol.getWorkHours());
        ticket.setWorkMinutes(protocol.getWorkMinutes());

        List<UUID> checkedIds = protocol.getChecklistItems().stream().filter(ChecklistItemDTO::isCompleted).map(ChecklistItemDTO::getId).toList();

        for (ChecklistItem item : ticket.getChecklistItems()) {
            item.setCompleted(checkedIds.contains(item.getId()));
        }
    }
}
