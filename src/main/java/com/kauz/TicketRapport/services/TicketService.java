package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.User;
import com.kauz.TicketRapport.models.filters.TicketFilter;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A repository specifically for querying ticket data.
 */
@Repository
public class TicketService extends DBService<Ticket> {

    @Transactional
    public Stream<Ticket> find(TicketFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        addFilter(cb, cq, root, filter);

        return em.createQuery(cq).getResultStream();
    }

    private void addFilter(CriteriaBuilder cb, CriteriaQuery<Ticket> cq, Root<Ticket> root, TicketFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            predicates.add(cb.or(
                    cb.like(root.join("assignedUser").get("firstname"), "%" + filter.getSearch() + "%"),
                    cb.like(root.join("assignedUser").get("lastname"), "%" + filter.getSearch() + "%")));
        }
        if (filter.getLearnerId() != null) {
            predicates.add(cb.equal(root.join("assignedUser").get("id"), filter.getLearnerId()));
        }
        if (filter.getClientId() != null) {
            predicates.add(cb.equal(root.join("client").get("id"), filter.getClientId()));
        }
        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            List<Predicate> statusPreds = new ArrayList<>();
            String[] statuses = filter.getStatus().split(",");
            for (String status : statuses) {
                statusPreds.add(cb.equal(root.join("status").get("description"), status));
            }
            predicates.add(cb.or(statusPreds.toArray(new Predicate[]{})));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[]{})));
    }
}
