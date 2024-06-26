package ch.kauz.ticketrapport.models.templates;

import ch.kauz.ticketrapport.models.base.DBEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a template checklist database entry.
 * <p>
 *     Must have an id and a title.
 *     <br>
 *     May be associated with any number of template checklist items.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TemplateChecklist implements DBEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Bitte geben Sie ein Titel an")
    @Size(max = 50, message = "Die Grösse muss zwischen 0 und 50 liegen")
    private String title;

    @OneToMany(mappedBy = "checklist", orphanRemoval = true)
    @OrderBy("ordinal")
    private Set<TemplateChecklistItems> items = new HashSet<>();

    public TemplateChecklist(String title, Set<TemplateChecklistItems> items) {
        this.title = title;
        this.items = items;
    }
}
