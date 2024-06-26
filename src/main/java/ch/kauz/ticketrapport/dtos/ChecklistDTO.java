package ch.kauz.ticketrapport.dtos;

import ch.kauz.ticketrapport.models.ChecklistItem;
import ch.kauz.ticketrapport.models.templates.TemplateChecklistItems;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Collection;
import java.util.List;

/**
 * Data Transfer Object representing a checklist.
 * <p>
 *     Contains information about the items as well as whether the checklist should be saved as a template and the title of the template.
 * </p>
 */
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ChecklistDTO {
    private boolean saveAsTemplate;

    @Size(max = 50)
    private String templateName;
    private List<ChecklistItemDTO> items;

    public static ChecklistDTO ofEntity(Collection<ChecklistItem> items) {
        return ChecklistDTO.builder()
                .items(items.stream().map(ChecklistItemDTO::ofEntity).toList())
                .build();
    }

    public static ChecklistDTO ofTemplateItems(Collection<TemplateChecklistItems> items) {
        return ChecklistDTO.builder()
                .items(items.stream().map(ChecklistItemDTO::ofEntity).toList())
                .build();
    }
}
