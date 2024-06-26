package ch.kauz.ticketrapport.models.templates;

import ch.kauz.ticketrapport.models.base.DBEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a template checklist item database entry.
 * <p>
 *     Must have an id and a description.
 *     <br>
 *     May be associated with any number of template checklists.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TemplateItem implements DBEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Bitte geben Sie eine Beschreibung an")
    @Size(max = 100, message = "Die Grösse muss zwischen 0 und 100 liegen")
    private String description;

    @OneToMany(mappedBy = "item", orphanRemoval = true)
    private Set<TemplateChecklistItems> checklists = new HashSet<>();

    public TemplateItem(String description, Set<TemplateChecklistItems> checklists) {
        this.description = description;
        this.checklists = checklists;
    }

    /* This method is called from a template, do not remove */
    public long getChecklistCount() {
        return checklists.stream().map(TemplateChecklistItems::getChecklist).distinct().count();
    }
}
