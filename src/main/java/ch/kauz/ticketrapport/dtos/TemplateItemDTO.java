package ch.kauz.ticketrapport.dtos;

import ch.kauz.ticketrapport.models.templates.TemplateChecklistItems;
import ch.kauz.ticketrapport.models.templates.TemplateItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data Transfer Object representing a template item.
 * <p>
 *     Contains information pertaining to the template item entity as well as a selected attribute,
 *     to track if the item is currently selected in a form list.
 * </p>
 */
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TemplateItemDTO {
    private String id;
    @NotBlank(message = "Bitte geben Sie eine Beschreibung an")
    @Size(max = 100, message = "Die Grösse muss zwischen 0 und 100 liegen")
    private String description;
    private int ordinal;
    private boolean selected;

    public static TemplateItemDTO ofEntity(TemplateItem entity) {
        return TemplateItemDTO.builder()
                .id(entity.getId().toString())
                .description(entity.getDescription())
                .build();
    }

    public static TemplateItemDTO ofEntity(TemplateChecklistItems entity) {
        return TemplateItemDTO.builder()
                .id(entity.getItem().getId().toString())
                .description(entity.getItem().getDescription())
                .selected(true)
                .ordinal(entity.getOrdinal())
                .build();
    }

    public static TemplateItemDTO ofJSONObject(JSONObject obj) throws JSONException {
        return TemplateItemDTO.builder()
                .id(obj.getString("id"))
                .description(obj.getString("description"))
                .ordinal(obj.getInt("ordinal"))
                .selected(true)
                .build();
    }
}
