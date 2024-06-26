package ch.kauz.ticketrapport.models.templates;

import ch.kauz.ticketrapport.models.base.DBEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a relationship between a template checklist and template item as an entity in the database.
 * <p>
 *     Must have an id and an ordinal.
 *     <br>
 *     Must be associated with a single template checklist and a single template item.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TemplateChecklistItems implements DBEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    private TemplateChecklist checklist;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    private TemplateItem item;

    @NotNull
    @Min(0)
    private int ordinal;

    public TemplateChecklistItems(TemplateChecklist checklist, TemplateItem item, int ordinal) {
        this.checklist = checklist;
        this.item = item;
        this.ordinal = ordinal;
    }
}
