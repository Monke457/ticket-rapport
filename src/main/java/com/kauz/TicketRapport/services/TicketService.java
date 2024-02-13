package com.kauz.TicketRapport.services;

import com.kauz.TicketRapport.models.Client;
import com.kauz.TicketRapport.models.Status;
import com.kauz.TicketRapport.models.Ticket;
import com.kauz.TicketRapport.models.User;
import com.kauz.TicketRapport.filters.Filter;
import com.kauz.TicketRapport.filters.TicketFilter;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * A repository specifically for querying ticket data.
 */
@Repository
public class TicketService extends DBService<Ticket> {

    @Override
    protected void addFilter(CriteriaBuilder cb, CriteriaQuery<Ticket> cq, Root<Ticket> root, Filter filter) {
        TicketFilter f = (TicketFilter) filter;
        List<Predicate> predicates = new ArrayList<>();
        Join<Ticket, Status> statusJoin = root.join("status", JoinType.LEFT);
        Join<Ticket, User> userJoin = root.join("assignedUser", JoinType.LEFT);
        if (f.getSearch() != null && !f.getSearch().isBlank()) {
            predicates.add(cb.or(
                    cb.like(userJoin.get("fullName"), "%" + f.getSearch() + "%"),
                    cb.like(root.get("title"), "%" + f.getSearch() + "%"),
                    cb.like(root.get("description"), "%" + f.getSearch() + "%")));
        }
        if (f.getLearnerId() != null) {
            predicates.add(cb.equal(userJoin.get("id"), f.getLearnerId()));
        }
        if (f.getClientId() != null) {
            Join<Ticket, Client> clientJoin = root.join("client", JoinType.LEFT);
            predicates.add(cb.equal(clientJoin.get("id"), f.getClientId()));
        }
        if (f.getStatusId() != null) {
            predicates.add(cb.equal(statusJoin.get("id"), f.getStatusId()));
        }
        if (f.getStatus() != null && !f.getStatus().isBlank()) {
            List<Predicate> statusPreds = new ArrayList<>();
            String[] statuses = f.getStatus().split(",");
            for (String status : statuses) {
                statusPreds.add(cb.equal(statusJoin.get("description"), status));
            }
            predicates.add(cb.or(statusPreds.toArray(new Predicate[]{})));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[]{})));
    }
}
