package com.kauz.TicketRapport.model;

import com.kauz.TicketRapport.model.helpers.DBEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role implements DBEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String description;

    public Role(String description) {
        this.description = description;
    }
}
