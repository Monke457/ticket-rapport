package ch.kauz.ticketrapport.models;

import ch.kauz.ticketrapport.models.base.DBEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a status database entry.
 * <p>
 *     Must have an id and a description.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Status implements DBEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @NotBlank
    private String description;

    public Status(String description) {
        this.description = description;
    }
}
