package com.kauz.TicketRapport.models;

import com.kauz.TicketRapport.models.helpers.DBEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user in the database.
 * Must have an id and email.
 * May have a firstname, lastname, and password.
 * The password must be hashed using a BCryptEncoder.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User implements DBEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String firstname;
    private String lastname;

    @Column(unique = true)
    private String email;
    private String password;

    @ManyToOne
    private Role role;

    @Transient
    private String fullName;
    public User(String firstname, String lastname, String email, String password, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getFullName() {
        return firstname + ' ' + lastname;
    }

    public boolean isAdmin() {
        return Objects.equals(role.getDescription(), "ADMIN");
    }
}
