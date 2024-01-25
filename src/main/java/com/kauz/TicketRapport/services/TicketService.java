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

    @Transactional
    public Stream<Ticket> findBroadSearch(TicketFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        addFilterBroad(cb, cq, root, filter);

        return em.createQuery(cq).getResultStream();
    }

    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<Ticket> cq, Root<Ticket> root, Filter filter) {
        List<Predicate> predicates = getPredicates(cb, root, (TicketFilter) filter);
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            predicates.add(cb.or(
                    cb.like(root.join("assignedUser").get("firstname"), "%" + filter.getSearch() + "%"),
                    cb.like(root.join("assignedUser").get("lastname"), "%" + filter.getSearch() + "%")));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[]{})));
    }

    private void addFilterBroad(CriteriaBuilder cb, CriteriaQuery<Ticket> cq, Root<Ticket> root, Filter filter) {
        List<Predicate> predicates = getPredicates(cb, root, (TicketFilter) filter);
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            predicates.add(cb.or(
                    cb.like(root.join("assignedUser").get("fullName"), "%" + filter.getSearch() + "%"),
                    //cb.like(root.join("assignedUser").get("firstname"), "%" + filter.getSearch() + "%"),
                    //cb.like(root.join("assignedUser").get("lastname"), "%" + filter.getSearch() + "%"),
                    cb.like(root.get("title"), "%" + filter.getSearch() + "%"),
                    cb.like(root.get("description"), "%" + filter.getSearch() + "%")));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[]{})));
    }

    private List<Predicate> getPredicates(CriteriaBuilder cb, Root<Ticket> root, TicketFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
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
        return predicates;
    }
}
