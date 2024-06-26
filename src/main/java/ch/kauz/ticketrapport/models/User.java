package ch.kauz.ticketrapport.models;

import ch.kauz.ticketrapport.models.base.DBEntity;
import ch.kauz.ticketrapport.models.helpers.RoleType;
import ch.kauz.ticketrapport.validation.constraints.ValidRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a user database entry.
 * <p>
 *     Must have an id, firstname, lastname, email, password and role.
 *     <br>
 *     May be assigned any number of tickets.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User implements DBEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Bitte geben Sie einen Vorname an")
    @Size(max = 50, message = "Die Grösse muss zwischen 0 und 50 leigen")
    private String firstname;

    @NotBlank(message = "Bitte geben Sie einen Nachname an")
    @Size(max = 50, message = "Die Grösse muss zwischen 0 und 50 leigen")
    private String lastname;

    @Column(unique = true)
    @Email(message = "Bitte geben Sie eine gültige Email-Adresse an")
    @NotBlank(message = "Bitte geben Sie eine gültige Email-Adresse an")
    @Size(max = 50, message = "Die Grösse muss zwischen 0 und 50 leigen")
    private String email;
    private String password;

    @ManyToOne
    @ValidRole
    private Role role;

    @OneToMany(mappedBy = "learner")
    @OrderBy("client")
    private Set<Ticket> tickets = new HashSet<>();

    @Formula("concat(firstname, ' ', lastname)")
    private String fullName;

    public User(String firstname, String lastname, String email, String password, Role role, Set<Ticket> tickets) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.tickets = tickets;
    }

    public boolean isAdmin() {
        if (role == null) {
            return false;
        }
        return role.getDescription().equals(RoleType.ADMIN.getDisplay());
    }

    /**
     * Sets the learner to null in associated tickets before deletion. This method is called automatically, do not remove.
     */
    @PreRemove
    public void unAssignTickets() {
        for (Ticket t : tickets) {
            t.setLearner(null);
        }
    }
}
