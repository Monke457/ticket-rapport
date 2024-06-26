package ch.kauz.ticketrapport.models;

import ch.kauz.ticketrapport.models.base.DBEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a checklist item database entry.
 * <p>
 *     Must have an id, ordinal, description, and completion status.
 *     <br>
 *     Must be associated with a single ticket.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItem implements DBEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Min(0)
    private int ordinal;
    @NotBlank(message = "Bitte geben Sie eine Beschreibung an")
    private String description;

    @NotNull
    private boolean completed;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Ticket ticket;

    public ChecklistItem(int ordinal, String description, boolean completed, Ticket ticket) {
        this.ordinal = ordinal;
        this.description = description;
        this.completed = completed;
        this.ticket = ticket;
    }
}
