package ch.kauz.ticketrapport.models;

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
 * Represents a client database entry.
 * <p>
 *     Must have an id and a name.
 *     <br>
 *     May be associated with any number of tickets.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client implements DBEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Bitte geben Sie einen Name ean")
    @Size(max = 50, message = "Die Grösse muss zwischen 0 und 50 liegen")
    private String name;

    @OneToMany(mappedBy = "client")
    @OrderBy("learner")
    private Set<Ticket> tickets = new HashSet<>();

    public Client(String name) {
        this.name = name;
    }

    /**
     * Sets client to null in associated tickets before deletion. This method is called automatically, do not remove.
     */
    @PreRemove
    public void unAssignTickets() {
        for (Ticket t : tickets) {
            t.setClient(null);
        }
    }
}
