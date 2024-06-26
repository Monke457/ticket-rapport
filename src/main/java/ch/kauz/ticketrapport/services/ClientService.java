package ch.kauz.ticketrapport.services;

import ch.kauz.ticketrapport.filters.Filter;
import ch.kauz.ticketrapport.models.Client;
import ch.kauz.ticketrapport.services.base.DBService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

/**
 * Repository for mapping client data between the application and the persistence context.
 * <p>
 *     Inherits all query methods from the parent repository.
 * </p>
 *
 * @see DBService
 */
@Repository
public class ClientService extends DBService<Client> {

    /**
     * Adds a name filter to the query.
     * @see DBService#addFilter
     */
    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<Client> cq, Root<Client> root, Filter filter) {
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            cq.where(cb.like(root.get("name"), "%" + filter.getSearch() + "%"));
        }
    }
}
