package ch.kauz.ticketrapport.dtos;

import ch.kauz.ticketrapport.models.templates.TemplateChecklistItems;
import lombok.*;

import java.util.Collection;
import java.util.List;

/**
 * Data Transfer Object representing the associated template items of a template checklist.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TemplateChecklistItemsDTO {
    private List<TemplateItemDTO> items;

    public static TemplateChecklistItemsDTO ofEntity(Collection<TemplateChecklistItems> items) {
        return TemplateChecklistItemsDTO.builder()
                .items(items.stream().map(TemplateItemDTO::ofEntity).toList())
                .build();
    }

    /* This method is called from a template, do not remove */
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }
}
