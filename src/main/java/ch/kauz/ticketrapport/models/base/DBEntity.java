package ch.kauz.ticketrapport.models.base;

import ch.kauz.ticketrapport.services.base.DBService;

import java.util.UUID;

/**
 * Base database entity interface - contains id getter.
 * <p>
 *     All persisted entity models must implement this interface in order to be compatible with the service queries.
 * </p>
 *
 * @see DBService
 */
public interface DBEntity {
    UUID getId();
}
