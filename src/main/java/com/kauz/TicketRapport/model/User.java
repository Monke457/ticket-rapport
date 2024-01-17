package com.kauz.TicketRapport.model;

import com.kauz.TicketRapport.model.helpers.DBEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

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
    private String email;

    @ManyToOne
    private Role role;

    public User(String firstname, String lastname, String email, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
    }
}
