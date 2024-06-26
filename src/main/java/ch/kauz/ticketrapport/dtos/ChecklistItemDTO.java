package ch.kauz.ticketrapport.dtos;

import ch.kauz.ticketrapport.models.ChecklistItem;
import ch.kauz.ticketrapport.models.templates.TemplateChecklistItems;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Data Transfer Object representing a checklist item.
 * <p>
 *     Contains information pertaining to the checklist item entity as well as an optional template item id.
 *     <br>
 *     The template item id is stored in case the item has been created from a template item and
 *     then used to create a new template. This way redundancies are avoided and
 *     we don't create duplicate templates items.
 * </p>
 * @see ChecklistItem
 */
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ChecklistItemDTO {
    private UUID id;

    @Size(max = 100, message = "Die Grösse muss zwischen 0 und 100 liegen")
    private String description;
    private int ordinal;
    private boolean completed;

    //track the template item id if the item has been generated from a template
    //in case of creating a new template with existing template items (we do not want duplicates)
    private UUID templateItemId;

    public static ChecklistItemDTO ofEntity(ChecklistItem entity) {
        return ChecklistItemDTO.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .ordinal(entity.getOrdinal())
                .completed(entity.isCompleted())
                .build();
    }

    public static ChecklistItemDTO ofEntity(TemplateChecklistItems entity) {
        return ChecklistItemDTO.builder()
                .templateItemId(entity.getItem().getId())
                .description(entity.getItem().getDescription())
                .ordinal(entity.getOrdinal())
                .build();
    }

    public static ChecklistItemDTO ofJSONObject(JSONObject obj) throws JSONException {
        String id = obj.getString("id");
        String templateItemId = obj.getString("templateItemId");
        return ChecklistItemDTO.builder()
                .id(id.length() > 5 ? UUID.fromString(id) : null)
                .description(obj.getString("description"))
                .ordinal(obj.getInt("ordinal"))
                .completed(obj.getBoolean("completed"))
                .templateItemId(templateItemId.length() > 5 ? UUID.fromString(templateItemId) : null)
                .build();
    }
}
