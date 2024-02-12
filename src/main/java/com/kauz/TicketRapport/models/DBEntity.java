package com.kauz.TicketRapport.models;

import java.util.UUID;

/**
 * A base database entity interface which all entities should inherit from.
 * Enables entities to be used in the DBService repository as a generic class.
 */
public interface DBEntity {
    UUID getId();
}
