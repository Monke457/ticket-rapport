package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.filters.Filter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

/**
 * A repository specifically for querying client data.
 */
@Repository
public class ClientService extends DBService<Client> {

    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<Client> cq, Root<Client> root, Filter filter) {
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            cq.where(cb.like(root.get("name"), "%" + filter.getSearch() + "%"));
        }
    }
}
