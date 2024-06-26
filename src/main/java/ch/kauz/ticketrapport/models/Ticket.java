package ch.kauz.ticketrapport.models;

import ch.kauz.ticketrapport.models.base.DBEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a ticket database entry.
 * <p>
 *     Must have an id and title
 *     <br>
 *     May have a description, protocol, solution, workHours, workMinutes, assigned learner, status, and client.
 *     <br>
 *     May contain any number of checklist items.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ticket implements DBEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Bitte geben Sie einen Titel an")
    @Size(max = 50, message = "Die Grösse muss zwischen 0 und 50 liegen")
    private String title;

    @Size(max = 100, message = "Die Grösse muss zwischen 0 und 100 liegen")
    private String description;

    @Size(max = 2000)
    private String protocol;
    @Size(max = 2000)
    private String solution;

    @Min(0)
    @Max(99)
    private int workHours;

    @Min(0)
    @Max(59)
    private int workMinutes;

    @ManyToOne
    private User learner;

    @ManyToOne
    private Status status;

    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordinal")
    private Set<ChecklistItem> checklistItems = new HashSet<>();

    public Ticket(String title, String description, String protocol, String solution, int workHours, int workMinutes, User learner, Status status, Client client, Set<ChecklistItem> checklistItems) {
        this.title = title;
        this.description = description;
        this.protocol = protocol;
        this.solution = solution;
        this.workHours = workHours;
        this.workMinutes = workMinutes;
        this.learner = learner;
        this.status = status;
        this.client = client;
        this.checklistItems = checklistItems;
    }
}
