package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.filters.Filter;
import com.kauz.TicketRapport.models.filters.TicketFilter;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A repository specifically for querying ticket data.
 */
@Repository
public class TicketService extends DBService<Ticket> {

    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<Ticket> cq, Root<Ticket> root, Filter filter) {
        TicketFilter f = (TicketFilter) filter;
        List<Predicate> predicates = new ArrayList<>();
        if (f.getSearch() != null && !f.getSearch().isBlank()) {
            predicates.add(cb.or(
                    cb.like(root.join("assignedUser").get("firstname"), "%" + f.getSearch() + "%"),
                    cb.like(root.join("assignedUser").get("lastname"), "%" + f.getSearch() + "%")));
        }
        if (f.getLearnerId() != null) {
            predicates.add(cb.equal(root.join("assignedUser").get("id"), f.getLearnerId()));
        }
        if (f.getClientId() != null) {
            predicates.add(cb.equal(root.join("client").get("id"), f.getClientId()));
        }
        if (f.getStatus() != null && !f.getStatus().isBlank()) {
            List<Predicate> statusPreds = new ArrayList<>();
            String[] statuses = f.getStatus().split(",");
            for (String status : statuses) {
                statusPreds.add(cb.equal(root.join("status").get("description"), status));
            }
            predicates.add(cb.or(statusPreds.toArray(new Predicate[]{})));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[]{})));
    }
}
